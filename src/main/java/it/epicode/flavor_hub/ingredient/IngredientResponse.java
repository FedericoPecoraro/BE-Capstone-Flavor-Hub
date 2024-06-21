package it.epicode.flavor_hub.ingredient;

import lombok.Data;

@Data
public class IngredientResponse {
    private Long id;
    private String name;
    private String category;
    private int kcal;
}
