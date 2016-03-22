package eu.dominiktousek.pv168.cookbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * Implementation of manager responsible for CRUD operations on Ingredient.
 * Based on SQL database storage
 * 
 * @author Dominik Tousek (422385)
 */
public class IngredientManagerImpl implements IngredientManager {

    private final DataSource dataSource;
    
    
    /**
     * Creates instance of IngredientManager with given DataSource
     * 
     * @param dataSource    DataSource to be used
     */
    public IngredientManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createIngredient(Ingredient ingredient) throws ServiceFailureException {
        validate(ingredient,true);
        
        if(ingredient.getId()!=null){
            throw new IllegalArgumentException("Id of ingredient was already set!");
        }
        
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Ingredient (NAME,LOWERNAME) VALUES(?,LOWER(?))", 
                        Statement.RETURN_GENERATED_KEYS);){
            statement.setString(1,ingredient.getName());
            statement.setString(2,ingredient.getName());
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
            ingredient.setId(id);
            
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while creating new ingredient '" + ingredient + "'",ex);
        }
    }

    @Override
    public void updateIngredient(Ingredient ingredient) throws ServiceFailureException, EntityNotFoundException {
        validate(ingredient);
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE Ingredient SET NAME=?, LOWERNAME=LOWER(?) WHERE ID=?")
                ){
            statement.setString(1,ingredient.getName());
            statement.setString(2,ingredient.getName());
            statement.setLong(3,ingredient.getId());
            
            int count = statement.executeUpdate();
            if(count==0){
                throw new EntityNotFoundException("Entity '"+ ingredient +"' not found during update.");
            }
            else if(count>1){
                throw new ServiceFailureException("More than one record affected per one UPDATE! Database is broken.");
            }            
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while updating ingredient '" + ingredient + "'",ex);
        }
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws ServiceFailureException, EntityNotFoundException{
        validate(ingredient);
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM Ingredient WHERE ID=?")
                ){
            
            statement.setLong(1,ingredient.getId());
            
            int count = statement.executeUpdate();
            if(count==0){
                throw new EntityNotFoundException("Entity '"+ ingredient +"' not found during remove.");
            }
            else if(count>1){
                throw new ServiceFailureException("More than one record affected per one DELETE! Database is broken.");
            }            
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while removing ingredient '" + ingredient + "'",ex);
        }
    }

    @Override
    public Ingredient getIngredientById(Long id) throws ServiceFailureException {
        if(id==null){
            throw new IllegalArgumentException("Argument id can't be null");
        }
        
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM Ingredient WHERE ID=?")
                ){
            
            statement.setLong(1,id);
            
            boolean hasResult = statement.execute();
            if(!hasResult){
                return null;
            }
            else{
                ResultSet set = statement.getResultSet();
                if(set.next()){
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(set.getLong("ID"));
                    ingredient.setName(set.getString("NAME"));
                    return ingredient;
                }
                if(set.next()){
                    throw new ServiceFailureException("More than one record retrieved from database for id="+id);
                }
            }            
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while retrieving ingredient with id " + id,ex);
        }
        return null;
    }

    @Override
    public List<Ingredient> getAllIngredients() throws ServiceFailureException {        
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM Ingredient")
                ){
            
            return parseRows(statement);
            
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while retrieving all ingredients",ex);
        }
    }
    
    @Override
    public List<Ingredient> searchByName(String name) throws ServiceFailureException {
        if(name==null){
            throw new IllegalArgumentException("name can't be null!");
        }
        
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM Ingredient WHERE NAME LIKE '?%'")
                ){
            
            statement.setString(1, name);
            
            return parseRows(statement);  
            
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while retrieving all ingredients",ex);
        }
    }

    /**
     * Executes given <b>PreparedStatement</b> and parses acquired <b>ResultSet</b> to list of <b>Ingredient</b>s
     * 
     * @param statement Statement to be executed 
     * @return  List of Ingredients parsed from ResultSet of given statement
     * @throws SQLException 
     */
    private List<Ingredient> parseRows(final PreparedStatement statement) throws SQLException {
        boolean hasResult = statement.execute();
        if(!hasResult){
            return new ArrayList();
        }
        else{
            ResultSet set = statement.getResultSet();
            ArrayList<Ingredient> items = new ArrayList<>();
            while(set.next()){
                Ingredient ingredient = new Ingredient();
                ingredient.setId(set.getLong("ID"));
                ingredient.setName(set.getString("NAME"));
                items.add(ingredient);
            }
            return items;
        }
    }
    
    /**
     * Validates given ingredient - tests for null values o object it self, 
     * name atribute and optionally an id. Name is also tested for empty value.
     * <b>If case of validation fail, IllegalArgumentException is thrown.</b>
     * 
     * @param ingredient        ingredient object to be tested
     * @throws IllegalArgumentException
     */
    private void validate(Ingredient ingredient){
        validate(ingredient,false);
    }
    
    /**
     * Validates given ingredient - tests for null values o object it self, 
     * name atribute and optionally an id. Name is also tested for empty value.
     * <b>If case of validation fail, IllegalArgumentException is thrown.</b>
     * 
     * @param ingredient        ingredient object to be tested
     * @param allowNullIdentity true - ingredient id wont be tested for null value | false - ingredient id will be tested for null value
     * @throws IllegalArgumentException
     */
    private void validate(Ingredient ingredient, boolean allowNullIdentity) throws IllegalArgumentException {
        if(ingredient==null){
            throw new IllegalArgumentException("Null ingredient entity supplied!");
        }
        if(ingredient.getName()==null){
            throw new IllegalArgumentException("Name of ingredient can't be null!");
        }
        if(ingredient.getName().isEmpty()){
            throw new IllegalArgumentException("Name of ingredient can't be empty!");
        }
        if((!allowNullIdentity)&&(ingredient.getId()==null)){
            throw new IllegalArgumentException("Id of ingredient can't be null!");
        }
    }
    
}
