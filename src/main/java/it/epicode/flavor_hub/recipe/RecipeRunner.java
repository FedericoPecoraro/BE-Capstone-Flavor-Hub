package it.epicode.flavor_hub.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RecipeRunner implements ApplicationRunner {

    @Autowired
    private RecipeRepository repository;

    @Autowired
    private RecipeService service;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(repository.count() == 0) {
            List<RecipeRequest> recipe = Arrays.asList(

            );

            recipe.forEach(recipeRequest ->  service.createRecipe(recipeRequest));
            System.out.println("Categoria inserita con successo");
        } else {
            System.out.println("Categoria già esistente");
        }
    }
}