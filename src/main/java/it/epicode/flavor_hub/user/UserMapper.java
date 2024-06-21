package it.epicode.flavor_hub.user;

import it.epicode.flavor_hub.security.RegisteredUserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.List;

@Component
public class UserMapper {
    public RegisteredUserDTO entityToDto(User entity) {
        if (entity == null) {
            return null;
        }
        RegisteredUserDTO response = new RegisteredUserDTO();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    public List<RegisteredUserDTO> entitiesToDtos(List<User> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        return entities.stream().map(this::entityToDto).toList();
    }

}
