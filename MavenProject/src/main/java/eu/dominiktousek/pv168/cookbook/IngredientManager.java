package eu.dominiktousek.pv168.cookbook;

import java.util.List;

/**
 * Interface for manager responsible for CRUD operations on Ingredient
 * 
 * @author Dominik Tousek (422385)
 */
public interface IngredientManager {
    
    /**
     * Creates record in storage for given ingredient object
     * Given ingredient is <b>always validated</b> for null value of it self, null name and empty name
     * 
     * @param ingredient    Ingredient to be stored. <b>Id of given ingredient must be null</b>
     * @throws ServiceFailureException
     */    
    void createIngredient(Ingredient ingredient);
    
    /**
     * Updates ingredient in storage by given ingredient object.
     * Given ingredient is <b>always validated</b> for null value of it self, null name, empty name and for null id value
     * 
     * @param ingredient    Ingredient to be saved
     * @throws ServiceFailureException
     * @throws EntityNotFoundException
     */
    void updateIngredient(Ingredient ingredient);
    
    /**
     * Removes given ingredient from storage.
     * Given ingredient is <b>always validated</b> for null value of it self, null name, empty name and for null id value
     * 
     * @param ingredient    Ingredient to be removed from storage
     * @throws ServiceFailureException
     * @throws EntityNotFoundException
     */
    void removeIngredient(Ingredient ingredient);
    
    /**
     * Retrieve Ingredient by given id. 
     * If more than one Ingredient for one id exists, ServiceFAilureException will be thrown.
     * If given id is null, IllegalArgumentException will be thrown
     * 
     * @param id    Ingredient identifier
     * @return      Ingredient object  corresponidng to given id if found | null if not found
     * @throws ServiceFailureException
     */
    Ingredient getIngredientById(Long id);
    
    /**
     * Retrieves all ingredients stored
     * 
     * @return  List of all stored ingredients
     * @throws ServiceFailureException
     */
    List<Ingredient> getAllIngredients();   
    
    /**
     * Searches ingredients by name
     * 
     * @param name  Name to by searched by in the storage
     * @return  List of ingredients matching given name
     * @throws ServiceFailureException
     */
    List<Ingredient> searchByName(String name);
    
}
