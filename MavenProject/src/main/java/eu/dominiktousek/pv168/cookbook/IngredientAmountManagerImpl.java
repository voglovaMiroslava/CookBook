package eu.dominiktousek.pv168.cookbook;

import eu.dominiktousek.pv168.cookbook.daocontext.DBDataSourceFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;

public class IngredientAmountManagerImpl implements IngredientAmountManager {

    private final DataSource dataSource;

    public IngredientAmountManagerImpl() {
        this(DBDataSourceFactory.getDataSource());
    }

    public IngredientAmountManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void addIngredientInRecipe(IngredientAmount amount)
            throws IllegalArgumentException, ServiceFailureException {
        validate(amount, false);

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
        validate(amount, true);

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
        
    }

    @Override
    public List<IngredientAmount> getIngredientsByRecipe(Long recipeId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public IngredientAmount getIngredientAmountById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
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
}
