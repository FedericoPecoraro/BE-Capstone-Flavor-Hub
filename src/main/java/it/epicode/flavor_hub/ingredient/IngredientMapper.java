package it.epicode.flavor_hub.ingredient;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class IngredientMapper {

    public IngredientResponse entityToDto(Ingredient entity) {
        if (entity == null) {
            return null;
        }
        IngredientResponse response = new IngredientResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    public List<IngredientResponse> entitiesToDtos(List<Ingredient> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        return entities.stream().map(this::entityToDto).toList();
    }

}
