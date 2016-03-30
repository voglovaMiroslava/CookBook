package eu.dominiktousek.pv168.cookbook;

import eu.dominiktousek.pv168.cookbook.daocontext.DBDataSourceFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class IngredientAmountManagerImpl implements IngredientAmountManager {

    private final DataSource dataSource;
    private final IngredientManager ingredientManager;

    public IngredientAmountManagerImpl() {
        this(DBDataSourceFactory.getDataSource());
    }

    public IngredientAmountManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        ingredientManager = new IngredientManagerImpl(dataSource);
    }

    @Override
    public void addIngredientInRecipe(IngredientAmount amount)
            throws IllegalArgumentException, ServiceFailureException {
        validate(amount, true);

        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO IngredientAmount (recipeid, ingredientid, amount) "
                        + "VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, amount.getRecipeId());
            ps.setLong(2, amount.getIngredient().getId());
            ps.setString(3, amount.getAmount());

            ps.executeUpdate();
            ResultSet key = ps.getGeneratedKeys();

            if (key.next()) {
                Long amountid = key.getLong(1);
                amount.setId(amountid);
            } else {
                throw new ServiceFailureException(
                        "IngredientAmount '" + amount + "' was not stored to database.");
            }

            if (key.next()) {
                throw new ServiceFailureException(
                        "More than one record of IngredientAmount created.");
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error occured while creating new IngredientAmount '" + amount + "'", ex);
        }
    }

    @Override
    public void updateIngredientInRecipe(IngredientAmount amount) {
        validate(amount, false);

        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE IngredientAmount SET "
                        + "recipeid=?, ingredientid=?, amount=? WHERE id=?")) {

            ps.setLong(1, amount.getRecipeId());
            ps.setLong(2, amount.getIngredient().getId());
            ps.setString(3, amount.getAmount());
            ps.setLong(4, amount.getId());

            int count = ps.executeUpdate();

            if (count == 0) {
                throw new EntityNotFoundException("IngredientAmount was not updated.");
            }

            if (count > 1) {
                throw new ServiceFailureException("More than one IngredientAmount was updated.");
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error occurred while updating IngredientAmount " + amount, ex);
        }
    }

    @Override
    public void deleteIngredientFromRecipe(Long amountId) {
        if(amountId==null){
            throw new IllegalArgumentException("Id can't be null!");
        }
        
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM INGREDIENTAMOUNT WHERE ID=?")
                ){
            
            statement.setLong(1,amountId);
            
            int count = statement.executeUpdate();
            if(count==0){
                throw new EntityNotFoundException("Entity with id '"+ amountId +"' not found during remove.");
            }
            else if(count>1){
                throw new ServiceFailureException("More than one record affected per one DELETE! Database is broken.");
            }            
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while removing IngredientAmount with id '" + amountId + "'",ex);
        }
    }

    @Override
    public List<IngredientAmount> getIngredientsByRecipe(Long recipeId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public IngredientAmount getIngredientAmountById(Long id) {
        if(id==null){
            throw new IllegalArgumentException("Argument id can't be null");
        }
        
        try(
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM INGREDIENTAMOUNT WHERE ID=?")
                ){
            
            statement.setLong(1,id);
            
            try(ResultSet set = statement.executeQuery()){
                if(set.next()){
                    IngredientAmount rv = fromResultSet(set);
                    if(set.next()){
                        throw new ServiceFailureException("More than one record retrieved from database for id="+id);
                    }
                    return rv;
                }
                else{
                    throw new EntityNotFoundException("Requested entity with id '"+id+"' was not found!");
                }
            }
        }catch(SQLException ex){
            System.err.println(ex);
            throw new ServiceFailureException("Error occured while retrieving ingredient with id " + id,ex);
        }
    }

    private void validate(IngredientAmount amount, Boolean allowNullId) throws IllegalArgumentException {
        if (amount == null) {
            throw new IllegalArgumentException("IngredientAmount can't be null.");
        }
        if (amount.getIngredient() == null) {
            throw new IllegalArgumentException("Ingredient in IngredientAmount can't be null.");
        }
        if (amount.getIngredient().getId() == null) {
            throw new IllegalArgumentException("Id of Ingredient in IngredientAmount can't be null.");
        }
        if (amount.getRecipeId() == null) {
            throw new IllegalArgumentException("RecipeId in IngredientAmount can't be null.");
        }
        if (amount.getAmount() == null) {
            throw new IllegalArgumentException("Amount in IngredientAmount can't be null.");
        }
        if (amount.getAmount().isEmpty()) {
            throw new IllegalArgumentException("Amount in IngredientAmount can't be empty.");
        }
        if (!allowNullId && amount.getId() == null) {
            throw new IllegalArgumentException("IngredientAmount should have id.");
        }
    }
    
    /**
     * Executes given <b>PreparedStatement</b> and parses acquired <b>ResultSet</b> to list of <b>IngredientAmount</b>s
     * 
     * @param statement Statement to be executed 
     * @return List of IngredientAmounts parsed from ResultSet of given statement
     * @throws SQLException 
     */
    private List<IngredientAmount> parseRows(final PreparedStatement statement) throws SQLException {
        try(ResultSet set = statement.executeQuery()){
            ArrayList<IngredientAmount> items = new ArrayList<>();

            while(set.next()){
                items.add(fromResultSet(set));
            }
            return items;
        }
    }
    
    /**
     * Parses one IngredientAmount object from ResultSet on actual cursor position
     * 
     * @param set ResultSet to load data from
     * @return Parsed IngredientAmount object
     * @throws SQLException 
     */
    private IngredientAmount fromResultSet(ResultSet set) throws SQLException{
        IngredientAmount ingredientAmount = new IngredientAmount();
        ingredientAmount.setId(set.getLong("ID"));
        ingredientAmount.setRecipeId(set.getLong("RECIPEID"));
        ingredientAmount.setAmount(set.getString("AMOUNT"));
        
        Long ingredientId = set.getLong("INGREDIENTID");
        
        ingredientAmount.setIngredient(ingredientManager.getIngredientById(ingredientId));
        
        return ingredientAmount;
    }
}
