package it.epicode.flavor_hub.user;

import it.epicode.flavor_hub.recipe.Recipe;
import it.epicode.flavor_hub.security.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String firstName;
    @Column(length = 50)
    private String lastName;
    private String username;
    private String email;
    @Column(length = 125)
    private String password;
    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER)
    private final List<Roles> roles = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_liked_recipe",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipe> likedRecipes = new ArrayList<>();


    public void anonymizeUser(){
        this.firstName = null;
        this.lastName = null;
        this.username = UUID.randomUUID().toString();
        this.email = null;
        this.password = null;
        this.avatar = null;
        this.roles.clear();
    }
}