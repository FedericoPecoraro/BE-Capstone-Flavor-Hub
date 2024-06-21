package it.epicode.flavor_hub.ingredient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    IngredientService service;

    @PostMapping("/ingredient")
    public ResponseEntity<IngredientResponse> create(@RequestBody IngredientRequest request){
        return ResponseEntity.ok(service.createIngredient(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponse> modify(@PathVariable Long id, @RequestBody IngredientRequest request){
        return ResponseEntity.ok(service.editIngredient(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok(service.deleteIngredient(id));
    }
}