package it.epicode.flavor_hub.utensil;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class UtensilMapper {

    public UtensilResponse entityToDto(Utensil entity) {
        if (entity == null) {
            return null;
        }
        UtensilResponse response = new UtensilResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    public List<UtensilResponse> entitiesToDtos(List<Utensil> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        return entities.stream().map(this::entityToDto).toList();
    }

}
