package it.epicode.flavor_hub.recipe;

import it.epicode.flavor_hub.tag.Tag;
import it.epicode.flavor_hub.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // Find recipes by user
    List<Recipe> findByUser(User user);

    // Find recipes by tag
    @Query("SELECT r FROM Recipe r JOIN r.tags t WHERE t = :tag")
    List<Recipe> findByTag(@Param("tag") Tag tag);

    // Search recipes by title or ingredients
    @Query("SELECT r FROM Recipe r JOIN r.ingredients i WHERE r.title LIKE %:query% OR i.name LIKE %:query%")
    List<Recipe> searchByTitleOrIngredients(@Param("query") String query);

    // Find recipes by utensil
    @Query("SELECT r FROM Recipe r JOIN r.utensils u WHERE u.name LIKE %:utensil%")
    List<Recipe> findByUtensil(@Param("utensil") String utensil);

    // Find recipes by ingredient
    @Query("SELECT r FROM Recipe r JOIN r.ingredients i WHERE i.name = :ingredientName")
    List<Recipe> findByIngredient(@Param("ingredientName") String ingredientName);

    // Find recipes by max time (sum of preparation time and cooking time)
    @Query("SELECT r FROM Recipe r WHERE (r.preparationTime + r.cookingTime) <= :maxTime")
    List<Recipe> findByTime(@Param("maxTime") int maxTime);
}
