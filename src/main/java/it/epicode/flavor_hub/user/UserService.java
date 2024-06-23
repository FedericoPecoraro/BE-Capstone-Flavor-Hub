package it.epicode.flavor_hub.user;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.epicode.flavor_hub.email.EmailService;
import it.epicode.flavor_hub.security.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final AuthenticationManager auth;
    private final JwtUtils jwt;
    private final EmailService emailService; // per gestire invio email di benvenuto
    private final Cloudinary cloudinary; // gestisce cloudinary
    private final UserMapper userMapper;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    public Optional<LoginResponseDTO> login(String username, String password) {
        try {
            //SI EFFETTUA IL LOGIN
            //SI CREA UNA AUTENTICAZIONE OVVERO L'OGGETTO DI TIPO AUTHENTICATION
            var a = auth.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            a.getAuthorities(); //SERVE A RECUPERARE I RUOLI/IL RUOLO

            //SI CREA UN CONTESTO DI SICUREZZA CHE SARA UTILIZZATO IN PIU OCCASIONI
            SecurityContextHolder.getContext().setAuthentication(a);

            var user = usersRepository.findOneByUsername(username).orElseThrow();
            var dto = LoginResponseDTO.builder()
                    .withUser(RegisteredUserDTO.builder()
                            .withId(user.getId())
                            .withFirstName(user.getFirstName())
                            .withLastName(user.getLastName())
                            .withEmail(user.getEmail())
                            .withRoles(user.getRoles())
                            .withUsername(user.getUsername())
                            .build())
                    .build();

            //UTILIZZO DI JWTUTILS PER GENERARE IL TOKEN UTILIZZANDO UNA AUTHENTICATION E LO ASSEGNA ALLA LOGINRESPONSEDTO
            dto.setToken(jwt.generateToken(a));

            return Optional.of(dto);
        } catch (NoSuchElementException e) {
            //ECCEZIONE LANCIATA SE LO USERNAME E SBAGLIATO E QUINDI L'UTENTE NON VIENE TROVATO
            log.error("User not found", e);
            throw new InvalidLoginException(username, password);
        } catch (AuthenticationException e) {
            //ECCEZIONE LANCIATA SE LA PASSWORD E SBAGLIATA
            log.error("Authentication failed", e);
            throw new InvalidLoginException(username, password);
        }
    }

    public RegisteredUserDTO register(RegisterUserDTO register) {
        if (usersRepository.existsByUsername(register.getUsername())) {
            throw new EntityExistsException("Utente gia' esistente");
        }
        if (usersRepository.existsByEmail(register.getEmail())) {
            throw new EntityExistsException("Email gia' registrata");
        }
        Roles roles = rolesRepository.findById(Roles.ROLES_USER).get();
        /*
        if(!rolesRepository.existsById(Roles.ROLES_USER)){
            roles = new Roles();
            roles.setRoleType(Roles.ROLES_USER);
        } else {
            roles = rolesRepository.findById(Roles.ROLES_USER).get();
        }

         */
        User u = new User();
        BeanUtils.copyProperties(register, u);
        u.setPassword(encoder.encode(register.getPassword()));
        u.getRoles().add(roles);
        usersRepository.save(u);
        RegisteredUserDTO response = new RegisteredUserDTO();
        BeanUtils.copyProperties(u, response);
        response.setRoles(List.of(roles));
        //  emailService.sendWelcomeEmail(u.getEmail());

        return response;
    }

    public RegisteredUserDTO registerAdmin(RegisterUserDTO register) {
        if (usersRepository.existsByUsername(register.getUsername())) {
            throw new EntityExistsException("Utente gia' esistente");
        }
        if (usersRepository.existsByEmail(register.getEmail())) {
            throw new EntityExistsException("Email gia' registrata");
        }
        Roles roles = rolesRepository.findById(Roles.ROLES_ADMIN).get();
        User u = new User();
        BeanUtils.copyProperties(register, u);
        u.setPassword(encoder.encode(register.getPassword()));
        u.getRoles().add(roles);
        usersRepository.save(u);
        RegisteredUserDTO response = new RegisteredUserDTO();
        BeanUtils.copyProperties(u, response);
        response.setRoles(List.of(roles));
        return response;
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Rimuovi l'avatar se presente
        String publicId = user.getAvatar();
        if (publicId != null && !publicId.isEmpty()) {
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                log.error("Failed to delete avatar from Cloudinary", e);
                throw new RuntimeException("Failed to delete avatar from Cloudinary", e);
            }
        }

        // Cancella l'utente
        //usersRepository.delete(user);
        //ANONIMIZZAZIONE UTENTE
        // Tutti i dati personali dell'utente verranno
        // cancellati ma non le ricette ad esso collegate

        user.anonymizeUser();
        usersRepository.save(user);
    }

    public RegisteredUserDTO updateUser(Long id, UpdateUserDTO updatedUser, User loggedUser) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        jwt.checkUserLoggedEqualOrAdmin(user, loggedUser);
        userMapper.updateUser(user, updatedUser);
        usersRepository.save(user);
        return userMapper.entityToDto(user);
    }

    public List<RegisteredUserDTO> getAllUsers() {
        return usersRepository.findAll().stream()
                .map(user -> {
                    RegisteredUserDTO dto = new RegisteredUserDTO();
                    BeanUtils.copyProperties(user, dto);
                    dto.setRoles(user.getRoles());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public String uploadAvatar(Long id, MultipartFile image) throws IOException {
        long maxFileSize = getMaxFileSizeInBytes();
        if (image.getSize() > maxFileSize) {
            throw new FileSizeExceededException("File size exceeds the maximum allowed size");
        }

        Optional<User> optionalUser = usersRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        String existingPublicId = user.getAvatar();
        if (existingPublicId != null && !existingPublicId.isEmpty()) {
            cloudinary.uploader().destroy(existingPublicId, ObjectUtils.emptyMap());
        }

        Map<String, Object> uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
        String publicId = (String) uploadResult.get("public_id");
        String url = (String) uploadResult.get("url");

        user.setAvatar(publicId);
        usersRepository.save(user);

        return url;
    }


// DELETE delete cloudinary file

    @Transactional
    public String deleteAvatar(Long id) throws IOException {
        Optional<User> optionalUser = usersRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        String publicId = user.getAvatar();
        if (publicId != null && !publicId.isEmpty()) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            user.setAvatar(null);
            usersRepository.save(user);
            return "Avatar deleted successfully";
        } else {
            return "No avatar found for deletion";
        }
    }


    // PUT update cloudinary file
    @Transactional
    public String updateAvatar(Long id, MultipartFile updatedImage) throws IOException {
        deleteAvatar(id);
        return uploadAvatar(id, updatedImage);
    }

    public long getMaxFileSizeInBytes() {
        String[] parts = maxFileSize.split("(?i)(?<=[0-9])(?=[a-z])");
        long size = Long.parseLong(parts[0]);
        String unit = parts[1].toUpperCase();
        switch (unit) {
            case "KB":
                size *= 1024;
                break;
            case "MB":
                size *= 1024 * 1024;
                break;
            case "GB":
                size *= 1024 * 1024 * 1024;
                break;
        }
        return size;
    }

    public Optional<User> findOneByUsername(String username) {
        return usersRepository.findOneByUsername(username);
    }

}
