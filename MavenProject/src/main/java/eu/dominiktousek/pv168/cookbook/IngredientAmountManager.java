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
    
    //List<Recipe> getRecipesByIngredients(List<Ingredient> ingredients);
    
}
