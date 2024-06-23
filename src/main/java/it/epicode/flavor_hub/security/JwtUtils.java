package it.epicode.flavor_hub.security;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.epicode.flavor_hub.user.User;
import it.epicode.flavor_hub.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Objects;

//CLASSE CHE GENERA E CONTROLLA IL TOKEN (JWT)
@Component
public class JwtUtils {

    @Value("${jwt.key}")
    private String securityKey;
    @Value("${jwt.expirationMs}")
    private long expirationMs;

    @Autowired
    UserRepository usersRepository;

    public String generateToken(Authentication auth) {
        byte[] keyBytes = securityKey.getBytes();
        Key key = Keys.hmacShaKeyFor(keyBytes);

        var user = (SecurityUserDetails) auth.getPrincipal();
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .issuer("MySpringApplication")
                .expiration(new Date(new Date().getTime() + expirationMs))
                .signWith(key)
                //ESISTONO I CLAIM OVVERO SONO INFORMAZIONI AGGIUNTIVE CHE POSOSNO ESSERE AGGIUNTE AL TOKEN .claim("professore dell'aula", "Mauro")
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            byte[] keyBytes = securityKey.getBytes();
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            //PRENDIAMO LA DATA DI SCADENZA DAL TOKEN
            Date expirationDate = Jwts.parser()
                    .verifyWith(key).build()
                    .parseSignedClaims(token).getPayload().getExpiration();

            //token valido fino a 2024-04-01
            //token verificato il 2024-06-13

            //token valido fino 2024-06-13 10:01:00
            //token verifcato il 2024-06-13 10:02:00
            //VERIFICHIAMO SE LA DATA DI SCADENZA TROVATA E PRIMA O DOPO LA DATA DI OGGI
            if (expirationDate.before(new Date()))
                throw new JwtException("Token expired");
            Jwts.parser()
                    .verifyWith(key).requireIssuer("MySpringApplication");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        byte[] keyBytes = securityKey.getBytes();
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.parser()
                .verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public User getUserFromToken(String token) {
        String username = getUsernameFromToken(token);
        return  usersRepository.findOneByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + username));
    }

    public User getUserFromRequest(HttpServletRequest request) {
        String token = Objects.requireNonNull(request.getHeader("Authorization")).replace("Bearer ", "");
        String username = getUsernameFromToken(token);
        return  usersRepository.findOneByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + username));
    }

    public void checkUserLoggedEqualOrAdmin(User user, User loggedUser){
        if (!user.equals(loggedUser) && loggedUser.getRoles().stream()
                .noneMatch(roles -> roles.getRoleType().equals(Roles.ROLES_ADMIN))) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }
}
