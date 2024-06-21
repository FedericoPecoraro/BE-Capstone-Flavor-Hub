package it.epicode.flavor_hub.recipe;

import it.epicode.flavor_hub.ingredient.Ingredient;
import it.epicode.flavor_hub.tag.Tag;
import it.epicode.flavor_hub.user.User;
import it.epicode.flavor_hub.utensil.Utensil;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "recipe_id", nullable = false)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "preparation_time")
    private int preparationTime;
    @Column(name = "cooking_time")
    private int cookingTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "recipe_ingredient",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private List<Ingredient> ingredients;

    @ManyToMany
    @JoinTable(name = "recipe_utensil",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "utensil_id"))
    private List<Utensil> utensils;

    @ManyToMany
    @JoinTable(name = "recipe_tag",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;
}
