package it.epicode.flavor_hub.recipe;

import lombok.Data;

@Data
public class LikeRecipeRequest {
    private Long userId;
    private Long recipeId;
}
