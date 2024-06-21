package it.epicode.flavor_hub.tag;

import it.epicode.flavor_hub.recipe.Recipe;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "tags")
public class Tag {
    // VEGAN, VEGETARIAN, GLUTEN_FREE
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "tag_id")
    private Long id;
    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Recipe> recipes;
}
