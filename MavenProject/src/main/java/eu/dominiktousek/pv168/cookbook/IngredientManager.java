/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dominiktousek.pv168.cookbook;

import java.util.List;

/**
 *
 * @author Dominik
 */
public interface IngredientManager {
    
    void createIngredient(Ingredient ingredient);
    
    void updateIngredient(Ingredient ingredient);
    
    void removeIngredient(Ingredient ingredient);
    
    Ingredient getIngredientById(Long id);
    
    List<Ingredient> getAllIngredients();   
    
    List<Ingredient> searchByName(String name);
    
}
