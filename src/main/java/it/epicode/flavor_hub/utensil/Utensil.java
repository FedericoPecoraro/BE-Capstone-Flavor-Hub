package it.epicode.flavor_hub.utensil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.flavor_hub.recipe.Recipe;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "utensils")
public class Utensil {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "utensil_id")
    private Long id;
    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "utensils")
    @JsonIgnore
    private List<Recipe> recipes;
}
