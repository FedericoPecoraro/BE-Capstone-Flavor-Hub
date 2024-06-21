package it.epicode.flavor_hub.recipe;

import it.epicode.flavor_hub.ingredient.Ingredient;
import it.epicode.flavor_hub.ingredient.IngredientMapper;
import it.epicode.flavor_hub.ingredient.IngredientResponse;
import it.epicode.flavor_hub.tag.TagMapper;
import it.epicode.flavor_hub.user.UserMapper;
import it.epicode.flavor_hub.utensil.UtensilMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class RecipeMapper {

    @Autowired
    IngredientMapper ingredientMapper;

    @Autowired
    UtensilMapper utensilMapper;

    @Autowired
    TagMapper tagMapper;

    @Autowired
    UserMapper userMapper;

    public RecipeResponse entityToDto(Recipe entity) {
        if (entity == null) {
            return null;
        }
        RecipeResponse response = new RecipeResponse();
        BeanUtils.copyProperties(entity, response);
        response.setIngredients(ingredientMapper.entitiesToDtos(entity.getIngredients()));
        response.setUtensils(utensilMapper.entitiesToDtos(entity.getUtensils()));
        response.setTags(tagMapper.entitiesToDtos(entity.getTags()));
        response.setUser(userMapper.entityToDto(entity.getUser()));
        return response;
    }

    public List<RecipeResponse> entitiesToDtos(List<Recipe> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        return entities.stream().map(this::entityToDto).toList();
    }
}
