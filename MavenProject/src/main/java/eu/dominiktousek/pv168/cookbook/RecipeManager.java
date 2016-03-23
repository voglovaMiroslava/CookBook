package eu.dominiktousek.pv168.cookbook;

import java.util.List;
import java.time.Duration;

/**
 * Interface for manager responsible for CRUD operations on Recipe
 * 
 * @author Dominik Tousek (422385)
 */
public interface RecipeManager {
    
    /**
     * Stores recipe in storage.
     * Given recipe is <b>always validated</b> for null value of it self, 
     * null or empty name and null or empty instructions.
     * <b>Id of recipe must be null.</b>
     * In case of validation error throws <b>IllegalArgumentException</b>
     * 
     * @param recipe Recipe to be stored
     * @throws IllegalArgumentException
     */
    void createRecipe(Recipe recipe);
    
    /**
     * Updates recipe in storage by given recipe object.
     * Given recipe is <b>always validated</b> for null value of it self, 
     * null or empty name, null or empty instructions and null id
     * In case of validation error throws <b>IllegalArgumentException</b>
     * 
     * @param recipe Recipe to be updated
     * @throws IllegalArgumentException
     */
    void updateRecipe(Recipe recipe);
    
    /**
     * Deletes recipe with specified id from storage
     * In case of validation error throws <b>IllegalArgumentException</b>
     * 
     * @param id    Identifier of recipe - can't be null
     * @throws IllegalArgumentException
     */
    void deleteRecipe(Long id);
    
    /**
     * Retrieves one recipe identified by given id
     * In case of validation error throws <b>IllegalArgumentException</b>
     * 
     * @param id    Identifier of recipe - can't be null
     * @return Recipe object if found | null when not found
     * @throws IllegalArgumentException
     */
    Recipe getRecipeById(Long id);
    
    /**
     * Retrieves all recipes in storage
     * 
     * @return List of all recipes in storage
     */
    List<Recipe> getAllRecipes();
    
    /**
     * Searches recipes by name/part of name.
     * In case of validation error throws <b>IllegalArgumentException</b>
     * 
     * @param name  Name/part of name of recipe - empty string => all recipes, null value not allowed
     * @return List of filtered recipes
     * @throws IllegalArgumentException
     */
    List<Recipe> searchByName(String name);
    
    /**
     * Searches recipes by specified duration interval.
     * In case of validation error throws <b>IllegalArgumentException</b>
     * 
     * @param durationFrom  Minimum duration of preparation - null => not included in query
     * @param durationTo    Maximum duration of preparation - null => not included in query
     * @return List of filtered recipes
     * @throws IllegalArgumentException
     */
    List<Recipe> searchByDuration(Duration durationFrom, Duration durationTo);
    
    /**
     * Searches recipes with specified properties. 
     * In case of validation error throws <b>IllegalArgumentException</b>
     * 
     * @param name          Name/part of name of recipe - can't be null, empty string => not included in query
     * @param durationFrom  Minimum duration of preparation - null => not included in query
     * @param durationTo    Maximum duration of preparation - null => not included in query
     * @param ingredients   List of ingredients which the recipe should contain - null => not included in query 
     * @return List of filtered recipes 
     * @throws IllegalArgumentException
     */
    List<Recipe> search(String name, Duration durationFrom, Duration durationTo, List<Ingredient> ingredients);
    
}
