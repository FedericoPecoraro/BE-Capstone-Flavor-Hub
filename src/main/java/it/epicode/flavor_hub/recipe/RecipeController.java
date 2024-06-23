package it.epicode.flavor_hub.recipe;

import it.epicode.flavor_hub.security.JwtUtils;
import it.epicode.flavor_hub.tag.Tag;
import it.epicode.flavor_hub.user.User;
import it.epicode.flavor_hub.user.UserService;
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

//    // Delete Recipe
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
//        String response = recipeService.deleteRecipe(id);
//        return ResponseEntity.ok(response);
//    }

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
    public ResponseEntity<List<RecipeResponse>> getRecipesByTag(@RequestParam Tag tag) {
        List<RecipeResponse> recipes = recipeService.getRecipesByTag(tag);
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
