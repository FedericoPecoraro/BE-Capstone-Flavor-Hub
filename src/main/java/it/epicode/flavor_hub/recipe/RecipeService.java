package it.epicode.flavor_hub.recipe;

import it.epicode.flavor_hub.ingredient.Ingredient;
import it.epicode.flavor_hub.ingredient.IngredientRepository;
import it.epicode.flavor_hub.security.JwtUtils;
import it.epicode.flavor_hub.user.UserRepository;
import it.epicode.flavor_hub.tag.Tag;
import it.epicode.flavor_hub.user.User;
import it.epicode.flavor_hub.tag.TagRepository;
import it.epicode.flavor_hub.utensil.Utensil;
import it.epicode.flavor_hub.utensil.UtensilRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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

//    // Delete Recipe
//    public String deleteRecipe(Long id) {
//        if (!repository.existsById(id)) {
//            throw new EntityNotFoundException("Ricetta non trovata");
//        }
//        repository.deleteById(id);
//        return "Ricetta eliminata correttamente";
//    }

    // Delete Recipe
    @Transactional
    public void deleteRecipe(Long id, User loggedUser) {
        Optional<Recipe> entityOptional = repository.findById(id);
        if (entityOptional.isEmpty()) {
            throw new EntityNotFoundException("Ricetta non trovata");
        }
        Recipe entity = entityOptional.get();
        jwt.checkUserLoggedEqualOrAdmin(entity.getUser(), loggedUser);
        repository.delete(entity);
    }

    // Get All Recipes
    public List<RecipeResponse> getAllRecipes() {
        List<Recipe> recipes = repository.findAll();
        return recipes.stream()
                .map(recipeMapper::entityToDto)
                .collect(Collectors.toList());
    }

    // Get Recipe by Name
    public List<RecipeResponse> getRecipeByName(String query) {
        List<Recipe> recipes = repository.searchByTitleOrIngredients(query);
        return recipes.stream().map(recipe -> {
            RecipeResponse recipeResponse = new RecipeResponse();
            BeanUtils.copyProperties(recipe, recipeResponse);
            return recipeResponse;
        }).collect(Collectors.toList());
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
    public List<RecipeResponse> getRecipesByTag(Tag tag) {
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
