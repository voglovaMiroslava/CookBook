/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dominiktousek.pv168.cookbook;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class RecipeManagerImpl implements RecipeManager {

    public void createRecipe(Recipe recipe) {
        //TODO
    }

    public void updateRecipe(Recipe recipe) {
        //TODO
    }

    public void deleteRecipe(Long id) {
        //TODO
    }

    public Recipe getRecipeById(Long id) {
        //TODO
        return null;
    }

    public List<Recipe> getAllRecipes() {
        //TODO
        return new ArrayList<>();
    }

    public List<Recipe> searchByName(String name) {
        //TODO
        return new ArrayList<>();
    }

    public List<Recipe> searchByDuration(Duration durationFrom, Duration durationTo) {
        //TODO
        return new ArrayList<>();
    }

    public List<Recipe> search(String name, Duration durationFrom, Duration durationTo, List<Ingredient> ingredients) {
        //TODO
        return new ArrayList<>();
    }
    
}
