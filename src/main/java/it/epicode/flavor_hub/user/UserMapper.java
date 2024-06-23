package it.epicode.flavor_hub.user;

import it.epicode.flavor_hub.security.RegisteredUserDTO;
import it.epicode.flavor_hub.security.UpdateUserDTO;
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

    public void updateUser(User oldUser, UpdateUserDTO newUser) {
        if (oldUser == null || newUser == null) {
            return;
        }
        BeanUtils.copyProperties(newUser, oldUser);
    }

}
