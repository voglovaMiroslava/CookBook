package eu.dominiktousek.pv168.cookbook;

import eu.dominiktousek.pv168.cookbook.daocontext.DBDataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * Implementation of manager responsible for CRUD operations on Recipe.
 * Based on SQL database storage
 *
 * @author Dominik Tousek (422385) & Miroslava Voglova (382579)
 */
public class RecipeManagerImpl implements RecipeManager {

    private final DataSource dataSource;

    public RecipeManagerImpl() {
        this(DBDataSourceFactory.getDataSource());
    }

    public RecipeManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createRecipe(Recipe recipe) {
        validate(recipe, true);

        if (recipe.getId() != null) {
            throw new IllegalArgumentException("Id of recipe was already set!");
        }
        //TODO:
        /*
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Recipe (NAME,LOWERNAME) VALUES(?,LOWER(?))", 
                        Statement.RETURN_GENERATED_KEYS);){
            statement.setString(1,recipe.getName());
            statement.setString(2,recipe.getName());
            int count = statement.executeUpdate();
            if(count!=1){
                throw new ServiceFailureException("No generated key retrieved from database!");
            }
            
            ResultSet set = statement.getGeneratedKeys();
            if(!set.next()){
                throw new ServiceFailureException("No generated key retrieved from database!");
            }
            Long id = set.getLong(1);
            if(set.next()){
                throw new ServiceFailureException("More than one record in database affected during one CREATE!");
            }
            recipe.setId(id);
            
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while creating new recipe '" + recipe + "'",ex);
        }*/
    }

    @Override
    public void updateRecipe(Recipe recipe) {
        validate(recipe);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE RECIPE SET name=?, instructions=?, duration=? WHERE id = ?")) {

            statement.setNString(1, recipe.getName());
            statement.setNString(2, recipe.getInstructions());
            statement.setLong(3, recipe.getDuration().toMinutes());
            statement.setLong(4, recipe.getId());

            int count = statement.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Recipe to update: " + recipe + "was not found.");
            }
            if (count > 1) {
                throw new ServiceFailureException("More than one ( " + count + " ) recipe updated!");
            }

        } catch (SQLException e) {
            throw new ServiceFailureException("Error occurred while updating recipe: " + recipe, e);
        }

    }

    @Override
    public void deleteRecipe(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id can't be null!");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM RECIPE WHERE id = ?")) {

            statement.setLong(1, id);
            int count = statement.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Recipe to delete with id: " + id + "was not found.");
            }
            if (count > 1) {
                throw new ServiceFailureException("More than one ( " + count + " ) recipe deleted!");
            }
        } catch (SQLException e) {
            throw new ServiceFailureException("Error occurred while removing recipe with id " + id, e);
        }
    }

    @Override
    public Recipe getRecipeById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id can't be null!");
        }
        //TODO
        return null;
    }

    @Override
    public List<Recipe> getAllRecipes() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM RECIPE")) {

            ResultSet rs = ps.executeQuery();
            List<Recipe> results = new ArrayList<>();

            while (rs.next()){
                results.add(recipeFromResultSet(rs));
            }

            return results;
        } catch (SQLException e) {
            throw new ServiceFailureException("Error occurred while retrieving all recipes.", e);
        }
    }

    @Override
    public List<Recipe> searchByName(String name) {
        return search(name, null, null, null);
    }

    @Override
    public List<Recipe> searchByDuration(Duration durationFrom, Duration durationTo) {
        return search("", durationFrom, durationTo, null);
    }

    @Override
    public List<Recipe> search(String name, Duration durationFrom, Duration durationTo, List<Ingredient> ingredients) {
        if (name == null) {
            throw new IllegalArgumentException("name can't be null");
        }
        //TODO
        return new ArrayList<>();
    }

    /**
     * Validates given recipe - tests for null values of object it self,
     * name atribute, instructions atribute and an id. Name and instructions are also tested for empty value.
     * <b>In case of validation fail, IllegalArgumentException is thrown.</b>
     *
     * @param recipe recipe object to be tested
     * @throws IllegalArgumentException
     */
    private void validate(Recipe recipe) {
        validate(recipe, false);
    }

    /**
     * Validates given recipe - tests for null values of object it self,
     * name atribute, instructions atribute and optionally an id. Name and instructions are also tested for empty value.
     * <b>In case of validation fail, IllegalArgumentException is thrown.</b>
     *
     * @param recipe            recipe object to be tested
     * @param allowNullIdentity true - recipe id wont be tested for null value | false - recipe id will be tested for null value
     * @throws IllegalArgumentException
     */
    private void validate(Recipe recipe, boolean allowNullIdentity) {
        /*
        id - optionally could be null
        name - can't be null or empty
        instructions - can't be null or empty
        duration - could be null
        */
        if (recipe == null) {
            throw new IllegalArgumentException("Null recipe entity supplied!");
        }
        if (recipe.getName() == null) {
            throw new IllegalArgumentException("Name of recipe can't be null!");
        }
        if (recipe.getName().isEmpty()) {
            throw new IllegalArgumentException("Name of recipe can't be empty!");
        }
        if (recipe.getInstructions() == null) {
            throw new IllegalArgumentException("Instructions of recipe can't be null!");
        }
        if (recipe.getInstructions().isEmpty()) {
            throw new IllegalArgumentException("Instructions of recipe can't be empty!");
        }
        if (!allowNullIdentity && recipe.getId() == null) {
            throw new IllegalArgumentException("Id of recipe can't be null!");
        }
    }

}
