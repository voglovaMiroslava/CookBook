package eu.dominiktousek.pv168.cookbook;

import java.util.List;
import java.time.Duration;

/**
 *
 * @author Dominik Tousek (422385)
 */
public interface RecipeManager {
    
    void createRecipe(Recipe recipe);
    
    void updateRecipe(Recipe recipe);
    
    void deleteRecipe(Long id);
    
    Recipe getRecipeById(Long id);
    
    List<Recipe> getAllRecipes();
    
    List<Recipe> searchByName(String name);
    
    List<Recipe> searchByDuration(Duration durationFrom, Duration durationTo);
    
    List<Recipe> search(String name, Duration durationFrom, Duration durationTo, List<Ingredient> ingredients);
    
}
