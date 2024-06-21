package it.epicode.flavor_hub.recipe;

import it.epicode.flavor_hub.ingredient.IngredientResponse;
import it.epicode.flavor_hub.security.RegisteredUserDTO;
import it.epicode.flavor_hub.tag.TagResponse;
import it.epicode.flavor_hub.utensil.UtensilResponse;
import lombok.Data;

import java.util.List;

@Data
public class RecipeResponse {

    private Long id;
    private String title;
    private String description;
    private int preparationTime;
    private int cookingTime;
    private RegisteredUserDTO user;
    private List<IngredientResponse> ingredients;
    private List<UtensilResponse> utensils;
    private List<TagResponse> tags;

}
