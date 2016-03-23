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
        
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Recipe (NAME,INSTRUCTIONS,DURATION) VALUES(?,?,?)", 
                        Statement.RETURN_GENERATED_KEYS);){
            statement.setString(1, recipe.getName());
            statement.setString(2, recipe.getInstructions());
            if(recipe.getDuration()!=null){
                statement.setLong(3, recipe.getDuration().toMinutes());
            }else{
                statement.setNull(3, java.sql.Types.BIGINT);
            }
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
        }
    }

    @Override
    public void updateRecipe(Recipe recipe) {
        validate(recipe);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE RECIPE SET name=?, instructions=?, duration=? WHERE id = ?")) {

            statement.setString(1, recipe.getName());
            statement.setString(2, recipe.getInstructions());
            if(recipe.getDuration()!=null){
                statement.setLong(3, recipe.getDuration().toMinutes());
            }
            else{
                statement.setNull(3, java.sql.Types.BIGINT);
            }
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
            throw new IllegalArgumentException("Argument id can't be null!");
        }

        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM Recipe WHERE ID=?")
                ){
            
            statement.setLong(1,id);
            
            boolean hasResult = statement.execute();
            if(!hasResult){
                return null;
            }
            else{
                ResultSet set = statement.getResultSet();
                if(set.next()){
                    return fromResultSet(set);
                }
                if(set.next()){
                    throw new ServiceFailureException("More than one record retrieved from database for id="+id);
                }
            }            
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while retrieving recipe with id " + id,ex);
        }
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
                results.add(fromResultSet(rs));
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
            throw new IllegalArgumentException("Name can't be null!");
        }
        
        StringBuilder builder = new StringBuilder(60);
        builder.append("SELECT * FROM Recipe WHERE 1=1");
        
        if(!name.isEmpty()){
            name = "%"+name+"%";
            builder.append(" AND NAME LIKE ?");
        }
        if(durationFrom!=null){
            builder.append(" AND DURATION > ");
            builder.append(durationFrom.toMinutes());
        }
        if(durationTo!=null){
            builder.append(" AND DURATION < ");
            builder.append(durationTo.toMinutes());
        }
        if(ingredients!=null && !ingredients.isEmpty()){
            builder.append(" AND ID IN (SELECT RECIPEID FROM INGREDIENTAMOUNT WHERE");
            
            boolean first=true;
            for(Ingredient item:ingredients){
                if(!first){
                    builder.append(" AND ");
                }
                else{
                    first=false;
                }
                builder.append("ID=");
                builder.append(item.getId().toString());
            }
            
            builder.append(")");
        }
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(builder.toString())){
            
            if(!name.isEmpty()){
                statement.setString(1, name);
            }
            return parseRows(statement);  
            
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while searching recipes", ex);
        }
    }

    /**
     * Executes given <b>PreparedStatement</b> and parses acquired <b>ResultSet</b> to list of <b>Recipe</b>s
     * 
     * @param statement Statement to be executed 
     * @return List of Recipes parsed from ResultSet of given statement
     * @throws SQLException 
     */
    private static List<Recipe> parseRows(final PreparedStatement statement) throws SQLException {
        boolean hasResult = statement.execute();
        if(!hasResult){
            return new ArrayList<>();
        }
        else{
            ResultSet set = statement.getResultSet();
            ArrayList<Recipe> items = new ArrayList<>();
            while(set.next()){
                items.add(fromResultSet(set));
            }
            return items;
        }
    }
    
    /**
     * Parses one Recipe object from ResultSet on actual cursor position
     * 
     * @param set ResultSet to load data from
     * @return Parsed Recipe object
     * @throws SQLException 
     */
    private static Recipe fromResultSet(ResultSet set) throws SQLException{
        Recipe recipe = new Recipe();
        recipe.setId(set.getLong("ID"));
        recipe.setName(set.getString("NAME"));
        recipe.setInstructions(set.getString("INSTRUCTIONS"));
        Long duration = set.getLong("DURATION");
        if(!set.wasNull()){
            recipe.setDuration(Duration.ofMinutes(duration));
        }
        
        return recipe;
    }
    
    /**
     * Validates given recipe - tests for null values of object it self, 
     * name atribute, instructions atribute and an id. Name and instructions are also tested for empty value.
     * <b>In case of validation fail, IllegalArgumentException is thrown.</b>
     *
     * @param recipe recipe object to be tested
     * @throws IllegalArgumentException
     */
    private  static void validate(Recipe recipe) {
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
    private  static void validate(Recipe recipe, boolean allowNullIdentity) {
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
