package eu.dominiktousek.pv168.cookbook;

import java.util.List;

/**
 *
 * @author Dominik
 */
public interface IngredientAmountManager {
    
    void addIngredientInRecipe(IngredientAmount amount);
    
    void updateIngredientInRecipe(IngredientAmount amount);
    
    void deleteIngredientFromRecipe(Long Id);
    
    List<IngredientAmount> getIngredientsByRecipe(Long recipeId);
    
    IngredientAmount getIngredientAmountById(Long id);
    //List<Recipe> getRecipesByIngredients(List<Ingredient> ingredients);
    
}
