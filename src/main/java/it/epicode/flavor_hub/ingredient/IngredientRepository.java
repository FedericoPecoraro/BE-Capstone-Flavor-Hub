package it.epicode.flavor_hub.ingredient;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
//    List<Ingredient> findByCategory(String category);
}
