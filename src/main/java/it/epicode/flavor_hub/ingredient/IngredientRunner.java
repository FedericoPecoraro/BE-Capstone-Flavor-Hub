package it.epicode.flavor_hub.ingredient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class IngredientRunner implements ApplicationRunner {

    @Autowired
    private IngredientRepository repository;

    @Autowired
    private IngredientService service;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(repository.count() == 0) {
            List<IngredientRequest> ingredient = Arrays.asList(

            );

            ingredient.forEach(ingredientRequest ->  service.createIngredient(ingredientRequest));
            System.out.println("Categoria inserita con successo");
        } else {
            System.out.println("Categoria già esistente");
        }
    }
}