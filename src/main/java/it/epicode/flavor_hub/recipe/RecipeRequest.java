package it.epicode.flavor_hub.recipe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private Integer preparationTime;
    @NotNull
    private Integer cookingTime;
    @NotNull
    private Long userId;
    @NotEmpty
    private List<Long> ingredientIds;

    private List<Long> utensilIds;
    private List<Long> tagIds;
}
