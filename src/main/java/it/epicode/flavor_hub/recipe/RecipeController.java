package it.epicode.flavor_hub.recipe;

import it.epicode.flavor_hub.ingredient.IngredientResponse;
import it.epicode.flavor_hub.security.JwtUtils;
import it.epicode.flavor_hub.tag.TagResponse;
import it.epicode.flavor_hub.user.User;
import it.epicode.flavor_hub.user.UserService;
import it.epicode.flavor_hub.utensil.UtensilResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwt;

    private final Long VEGAN_TAG_ID = 1L;
    private final Long VEGETARIAN_TAG_ID = 2L;
    private final Long GLUTEN_FREE_TAG_ID = 3L;

    // Create Recipe
    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeRequest recipeRequest) {
        RecipeResponse savedRecipe = recipeService.createRecipe(recipeRequest);
        return ResponseEntity.ok(savedRecipe);
    }

    // Edit Recipe
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> editRecipe(@PathVariable Long id, @RequestBody RecipeRequest recipeRequest, HttpServletRequest request) {
        RecipeResponse updatedRecipe = recipeService.editRecipe(id, recipeRequest, jwt.getUserFromRequest(request));
        return ResponseEntity.ok(updatedRecipe);
    }

    // Delete Recipe
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id, HttpServletRequest request) {
        User loggedUser = jwt.getUserFromRequest(request);
        recipeService.deleteRecipe(id, loggedUser);
        return ResponseEntity.ok("Ricetta eliminata con successo");
    }

    // Get All Recipes
    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAllRecipes() {
        List<RecipeResponse> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/user/recipes")
    public ResponseEntity<List<RecipeResponse>> getRecipesByLoggedUser(HttpServletRequest request) {
        User loggedUser = jwt.getUserFromRequest(request);
        List<RecipeResponse> recipes = recipeService.getRecipesByUser(loggedUser);
        return ResponseEntity.ok(recipes);
    }

    // Get Recipe by ID
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable Long id) {
        RecipeResponse recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientResponse>> getIngredients() {
        List<IngredientResponse> ingredients = recipeService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/utensils")
    public ResponseEntity<List<UtensilResponse>> getUtensils() {
        List<UtensilResponse> utensils = recipeService.getAllUtensils();
        return ResponseEntity.ok(utensils);
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> getTags() {
        List<TagResponse> tags = recipeService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    // Get Recipe by Name
    @GetMapping("/search")
    public ResponseEntity<List<RecipeResponse>> getRecipesByName(@RequestParam String query) {
        List<RecipeResponse> recipes = recipeService.getRecipeByName(query);
        return ResponseEntity.ok(recipes);
    }

    // Get Recipe by User
    @GetMapping("/user/{username}")
    public ResponseEntity<List<RecipeResponse>> getRecipesByUser(@PathVariable String username) {
        Optional<User> user = userService.findOneByUsername(username);
        return user.map(value -> ResponseEntity.ok(recipeService.getRecipesByUser(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get Recipe by Tag
    @GetMapping("/searchByTag")
    public ResponseEntity<List<RecipeResponse>> getRecipesByTag(@RequestParam Long tagId) {
        List<RecipeResponse> recipes = recipeService.getRecipesByTagId(tagId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/vegan")
    public ResponseEntity<List<RecipeResponse>> getVeganRecipes() {
        List<RecipeResponse> recipes = recipeService.getVeganRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/vegetarian")
    public ResponseEntity<List<RecipeResponse>> getVegetarianRecipes() {
        List<RecipeResponse> recipes = recipeService.getVegetarianRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/glutenFree")
    public ResponseEntity<List<RecipeResponse>> getGlutenFreeRecipes() {
        List<RecipeResponse> recipes = recipeService.getGlutenFreeRecipes();
        return ResponseEntity.ok(recipes);
    }

    // Get Recipe by Utensil
    @GetMapping("/searchByUtensil")
    public ResponseEntity<List<RecipeResponse>> getRecipesByUtensil(@RequestParam String utensil) {
        List<RecipeResponse> recipes = recipeService.getRecipesByUtensil(utensil);
        return ResponseEntity.ok(recipes);
    }

    // Get Recipe by Time
    @GetMapping("/searchByTime")
    public ResponseEntity<List<RecipeResponse>> getRecipesByTime(@RequestParam int maxTime) {
        List<RecipeResponse> recipes = recipeService.getRecipesByTime(maxTime);
        return ResponseEntity.ok(recipes);
    }

    // Get Recipe by Ingredient
    @GetMapping("/searchByIngredient")
    public ResponseEntity<List<RecipeResponse>> getRecipeByIngredient(@RequestParam String ingredientName) {
        List<RecipeResponse> recipes = recipeService.getRecipesByIngredient(ingredientName);
        return ResponseEntity.ok(recipes);
    }
}
