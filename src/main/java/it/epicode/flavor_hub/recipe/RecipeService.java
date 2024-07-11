package it.epicode.flavor_hub.recipe;

import it.epicode.flavor_hub.ingredient.Ingredient;
import it.epicode.flavor_hub.ingredient.IngredientRepository;
import it.epicode.flavor_hub.ingredient.IngredientResponse;
import it.epicode.flavor_hub.security.JwtUtils;
import it.epicode.flavor_hub.tag.TagResponse;
import it.epicode.flavor_hub.user.UserRepository;
import it.epicode.flavor_hub.tag.Tag;
import it.epicode.flavor_hub.user.User;
import it.epicode.flavor_hub.tag.TagRepository;
import it.epicode.flavor_hub.utensil.Utensil;
import it.epicode.flavor_hub.utensil.UtensilRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.epicode.flavor_hub.utensil.UtensilResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository repository;
    @Autowired
    private RecipeMapper recipeMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UtensilRepository utensilRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private JwtUtils jwt;

    // Create Recipe
    @Transactional
    public RecipeResponse createRecipe(@Valid RecipeRequest recipeRequest) {
        Recipe entity = recipeMapper.dtoToEntity(recipeRequest);
        repository.save(entity);
        return recipeMapper.entityToDto(entity);
    }

    // Edit Recipe
    @Transactional
    public RecipeResponse editRecipe(Long id, RecipeRequest recipeRequest, User loggedUser) {
        Optional<Recipe> entityOptional = repository.findById(id);
        if (entityOptional.isEmpty()) {
            throw new EntityNotFoundException("Ricetta non trovata");
        }
        Recipe entity = entityOptional.get();
        jwt.checkUserLoggedEqualOrAdmin(entity.getUser(), loggedUser);
        recipeMapper.updateRecipe(entity, recipeRequest);
        repository.save(entity);

        return recipeMapper.entityToDto(entity);
    }

    // Delete Recipe
    @Transactional
    public void deleteRecipe(Long id, User loggedUser) {
        Optional<Recipe> entityOptional = repository.findById(id);
        if (entityOptional.isEmpty()) {
            throw new EntityNotFoundException("Ricetta non trovata");
        }
        Recipe entity = entityOptional.get();
        jwt.checkUserLoggedEqualOrAdmin(entity.getUser(), loggedUser);
        entity.getIngredients().clear();
        entity.getUtensils().clear();
        entity.getTags().clear();
        repository.save(entity);
        repository.delete(entity);
    }

    // Get All Recipes
    public List<RecipeResponse> getAllRecipes() {
        List<Recipe> recipes = repository.findAll();
        return recipeMapper.entitiesToDtos(recipes);
    }

    public List<RecipeResponse> getUserRecipes(Long userId) {
        List<Recipe> recipes = repository.findRecipesByUserId(userId);
        return recipeMapper.entitiesToDtos(recipes);
    }


    // Aggiungi questo metodo al RecipeService
    public RecipeResponse getRecipeById(Long id) {
        Recipe recipe = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ricetta non trovata"));
        return recipeMapper.entityToDto(recipe);
    }


    // Get All Ingredients
    public List<IngredientResponse> getAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return ingredients.stream()
                .map(ingredient -> {
                    IngredientResponse response = new IngredientResponse();
                    response.setId(ingredient.getId());
                    response.setName(ingredient.getName());
                    response.setCategory(ingredient.getCategory());
                    response.setKcal(ingredient.getKcal());
                    return response;
                })
                .collect(Collectors.toList());
    }

    // Get All Utensils
    public List<UtensilResponse> getAllUtensils() {
        List<Utensil> utensils = utensilRepository.findAll();
        return utensils.stream()
                .map(utensil -> {
                    UtensilResponse response = new UtensilResponse();
                    response.setId(utensil.getId());
                    response.setName(utensil.getName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    // Get All Tags
    public List<TagResponse> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(tag -> {
                    TagResponse response = new TagResponse();
                    response.setId(tag.getId());
                    response.setName(tag.getName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    // Get Recipe by Name
    public List<RecipeResponse> getRecipeByName(String query) {
        List<Recipe> recipes = repository.fullTextSearchRecipe(query.toLowerCase());
        return recipeMapper.entitiesToDtos(recipes);
    }

    // Get Recipe by User
    public List<RecipeResponse> getRecipesByUser(User user) {
        List<Recipe> recipes = repository.findByUser(user);
        return recipes.stream().map(recipe -> {
            RecipeResponse recipeResponse = new RecipeResponse();
            BeanUtils.copyProperties(recipe, recipeResponse);
            return recipeResponse;
        }).collect(Collectors.toList());
    }

    // Get Recipe by Tag
    public List<RecipeResponse> getRecipesByTagId(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + tagId));
        List<Recipe> recipes = repository.findByTag(tag);
        return recipes.stream().map(recipe -> {
            RecipeResponse recipeResponse = new RecipeResponse();
            BeanUtils.copyProperties(recipe, recipeResponse);
            return recipeResponse;
        }).collect(Collectors.toList());
    }

    // Get Recipe by Utensil
    public List<RecipeResponse> getRecipesByUtensil(String utensil) {
        List<Recipe> recipes = repository.findByUtensil(utensil);
        return recipes.stream().map(recipe -> {
            RecipeResponse recipeResponse = new RecipeResponse();
            BeanUtils.copyProperties(recipe, recipeResponse);
            return recipeResponse;
        }).collect(Collectors.toList());
    }

    // Get Recipe by Ingredient
    public List<RecipeResponse> getRecipesByIngredient(String ingredientName) {
        List<Recipe> recipes = repository.findByIngredient(ingredientName);
        return recipes.stream().map(recipe -> {
            RecipeResponse recipeResponse = new RecipeResponse();
            BeanUtils.copyProperties(recipe, recipeResponse);
            return recipeResponse;
        }).collect(Collectors.toList());
    }

    // Get Recipe by Time
    public List<RecipeResponse> getRecipesByTime(int maxTime) {
        List<Recipe> recipes = repository.findByTime(maxTime);
        return recipes.stream().map(recipe -> {
            RecipeResponse recipeResponse = new RecipeResponse();
            BeanUtils.copyProperties(recipe, recipeResponse);
            return recipeResponse;
        }).collect(Collectors.toList());
    }

    // Upload Recipe Image
    public String uploadRecipeImage(String recipeName, MultipartFile file) throws IOException {
        var uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("public_id", recipeName + "_image"));
        return uploadResult.get("url").toString();
    }
}
