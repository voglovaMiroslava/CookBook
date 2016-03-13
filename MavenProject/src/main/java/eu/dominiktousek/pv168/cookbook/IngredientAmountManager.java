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
public interface IngredientAmountManager {
    
    void addIngredientInRecipe(IngredientAmount amount);
    
    void updateIngredientInRecipe(IngredientAmount amount);
    
    void deleteIngredientFromRecipe(IngredientAmount amount);
    
    List<IngredientAmount> getIngredientsByRecipe(Long recipeId);
    
    List<Recipe> getRecipesByIngredients(List<Ingredient> ingredients);
    
}
