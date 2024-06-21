package it.epicode.flavor_hub.ingredient;

//import com.capstone.flavor_hub.recipe.Recipe;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ingredient_id")
    private Long id;
    @Column(length = 500, unique = true)
    private String name;
    @Column(name = "category")
    private String category;
    @Column(name = "kcal")
    private int kcal;

//    @ManyToMany(mappedBy = "ingredients")
//    private List<Recipe> recipes;

}
