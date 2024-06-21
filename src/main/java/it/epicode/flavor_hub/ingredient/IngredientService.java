package it.epicode.flavor_hub.ingredient;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {

    @Autowired
    private  IngredientRepository repository;

    public IngredientResponse createIngredient(IngredientRequest ingredientRequest){
        Ingredient entity = new Ingredient();

        BeanUtils.copyProperties(ingredientRequest, entity);
        IngredientResponse postIngredientResponse = new IngredientResponse();

        BeanUtils.copyProperties(entity, postIngredientResponse);
        repository.save(entity);
        return postIngredientResponse;
    }

    public IngredientResponse editIngredient(Long id, IngredientRequest ingredientRequest){
        if(!repository.existsById(id)){
            throw new EntityNotFoundException("Ingrediente non trovato");
        }
        Ingredient entity = repository.findById(id).get();
        BeanUtils.copyProperties(ingredientRequest, entity);
        repository.save(entity);

        IngredientResponse IngredientResponse = new IngredientResponse();
        BeanUtils.copyProperties(entity, IngredientResponse);

        return  IngredientResponse;
    }

    public String deleteIngredient(Long id){
        if(!repository.existsById(id)){
            throw new EntityNotFoundException("Ingrediente non trovato");
        }
        repository.deleteById(id);
        return "Ingrediente eliminato correttamente";
    }
}