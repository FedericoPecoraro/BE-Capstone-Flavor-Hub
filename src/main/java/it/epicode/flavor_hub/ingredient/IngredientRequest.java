package it.epicode.flavor_hub.ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class IngredientRequest {
    private String name;
    private String category;
    private int kcal;
    private static List<Long> idIngredient = new ArrayList<>();

    // Metodo getter per idIngredient
    public static Iterable<Long> getIdIngredient() {
        return idIngredient;
    }
}
