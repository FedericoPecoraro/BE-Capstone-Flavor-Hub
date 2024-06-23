package it.epicode.flavor_hub.recipe;

import it.epicode.flavor_hub.ingredient.Ingredient;
import it.epicode.flavor_hub.ingredient.IngredientMapper;
import it.epicode.flavor_hub.ingredient.IngredientRepository;
import it.epicode.flavor_hub.ingredient.IngredientResponse;
import it.epicode.flavor_hub.security.UpdateUserDTO;
import it.epicode.flavor_hub.tag.TagMapper;
import it.epicode.flavor_hub.tag.TagRepository;
import it.epicode.flavor_hub.user.User;
import it.epicode.flavor_hub.user.UserMapper;
import it.epicode.flavor_hub.user.UserRepository;
import it.epicode.flavor_hub.utensil.UtensilMapper;
import it.epicode.flavor_hub.utensil.UtensilRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UtensilRepository utensilRepository;


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

    public Recipe dtoToEntity (RecipeRequest dto){

        if (dto == null) {
            return null;
        }
        Recipe entity = new Recipe();

        BeanUtils.copyProperties(dto, entity);

        List<Ingredient> ingredients = ingredientRepository.findAllById(dto.getIngredientIds());
        entity.setIngredients(ingredients);

        Optional<User> user = userRepository.findById(dto.getUserId());
        user.ifPresent(entity::setUser);

        if (!CollectionUtils.isEmpty(dto.getUtensilIds())) {
            entity.setUtensils(utensilRepository.findAllById(dto.getUtensilIds()));
        }
        if (!CollectionUtils.isEmpty(dto.getTagIds())){
            entity.setTags(tagRepository.findAllById(dto.getTagIds()));
        }

        return entity;
    }

    public void updateRecipe(Recipe oldRecipe, RecipeRequest newRecipe) {
        if (oldRecipe == null || newRecipe == null) {
            return;
        }
        BeanUtils.copyProperties(newRecipe, oldRecipe);

        List<Ingredient> ingredients = ingredientRepository.findAllById(newRecipe.getIngredientIds());
        oldRecipe.setIngredients(ingredients);

        if (!CollectionUtils.isEmpty(newRecipe.getUtensilIds())) {
            oldRecipe.setUtensils(utensilRepository.findAllById(newRecipe.getUtensilIds()));
        } else {
            oldRecipe.getUtensils().clear();
        }
        if (!CollectionUtils.isEmpty(newRecipe.getTagIds())){
            oldRecipe.setTags(tagRepository.findAllById(newRecipe.getTagIds()));
        } else {
            oldRecipe.getTags().clear();
        }
    }
}
