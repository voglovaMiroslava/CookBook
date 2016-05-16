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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of manager responsible for CRUD operations on Ingredient.
 * Based on SQL database storage
 *
 * @author Dominik Tousek (422385)
 */
public class IngredientManagerImpl implements IngredientManager {

    private final DataSource dataSource;
    final static Logger LOG = LoggerFactory.getLogger(IngredientManagerImpl.class);

    /**
     * Creates instance of IngredientManager with DataSource from config file
     */
    public IngredientManagerImpl() {
        this(DBDataSourceFactory.getDataSource());
    }

    /**
     * Creates instance of IngredientManager with given DataSource
     *
     * @param dataSource DataSource to be used
     */
    public IngredientManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createIngredient(Ingredient ingredient) throws ServiceFailureException {
        validate(ingredient, true);

        LOG.debug("Creating new Ingredient.");

        if (ingredient.getId() != null) {
            LOG.error("Error while creating Ingredient. Id of ingredient was already set.");
            throw new IllegalArgumentException("Id of ingredient was already set!");
        }

        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Ingredient (NAME,LOWERNAME) VALUES(?,LOWER(?))",
                        Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, ingredient.getName());
            statement.setString(2, ingredient.getName());
            int count = statement.executeUpdate();
            if (count != 1) {
                LOG.error("Error while creating Ingredient. No generated key retrieved from database.");
                throw new ServiceFailureException("No generated key retrieved from database!");
            }

            try (ResultSet set = statement.getGeneratedKeys()) {
                if (!set.next()) {
                    LOG.error("Error while creating Ingredient. No generated key retrieved from database.");
                    throw new ServiceFailureException("No generated key retrieved from database!");
                }
                Long id = set.getLong(1);
                if (set.next()) {
                    LOG.error("Error while creating Ingredient. More than one record in database affected during one CREATE.");
                    throw new ServiceFailureException("More than one record in database affected during one CREATE!");
                }
                ingredient.setId(id);
            }

        } catch (SQLException ex) {
            LOG.error("SQL Exception occured while creating Ingredient. Message of exception: {}", ex.getMessage());
            throw new ServiceFailureException("Error occured while creating new ingredient '" + ingredient + "'", ex);
        }
    }

    @Override
    public void updateIngredient(Ingredient ingredient) throws ServiceFailureException, EntityNotFoundException {
        validate(ingredient);

        LOG.debug("Updating Ingredient.");

        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE Ingredient SET NAME=?, LOWERNAME=LOWER(?) WHERE ID=?")) {
            statement.setString(1, ingredient.getName());
            statement.setString(2, ingredient.getName());
            statement.setLong(3, ingredient.getId());

            int count = statement.executeUpdate();
            if (count == 0) {
                LOG.error("Error while updating Ingredient. Ingredient not found during update.");
                throw new EntityNotFoundException("Entity '" + ingredient + "' not found during update.");
            } else if (count > 1) {
                LOG.error("Error while updating Ingredient. More than one record affected per one UPDATE! Database is broken.");
                throw new ServiceFailureException("More than one record affected per one UPDATE! Database is broken.");
            }
        } catch (SQLException ex) {
            LOG.error("SQL Exception occured while updating Ingredient. Message of exception: {}", ex.getMessage());
            throw new ServiceFailureException("Error occured while updating ingredient '" + ingredient + "'", ex);
        }
    }

    @Override
    public void deleteIngredient(Long id) throws ServiceFailureException, EntityNotFoundException {
        if (id == null) {
            LOG.error("Error while deleting Ingredient by id. Id is null.");
            throw new IllegalArgumentException("Id can't be null!");
        }

         LOG.debug("Deleting Ingredient.");
        
        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM Ingredient WHERE ID=?")) {

            statement.setLong(1, id);

            int count = statement.executeUpdate();
            if (count == 0) {
                LOG.error("Error while deleting Ingredient. Entity with id {} not found.", id);
                throw new EntityNotFoundException("Entity with id '" + id + "' not found during remove.");
            } else if (count > 1) {
                LOG.error("Error while deleting Ingredient. More than one record affected per one DELETE! Database is broken.");
                throw new ServiceFailureException("More than one record affected per one DELETE! Database is broken.");
            }
        } catch (SQLException ex) {
            LOG.error("SQL Exception occured while deleting Ingredient. Message of exception: {}", ex.getMessage());
            throw new ServiceFailureException("Error occured while removing ingredient with id '" + id + "'", ex);
        }
    }

    @Override
    public Ingredient getIngredientById(Long id) throws ServiceFailureException {
        if (id == null) {
            LOG.error("Error while getting Ingredient by id. Id is null.");
            throw new IllegalArgumentException("Argument id can't be null");
        }

        LOG.debug("Getting Ingredient by id.");
        
        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM Ingredient WHERE ID=?")) {

            statement.setLong(1, id);

            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    Ingredient rv = fromResultSet(set);
                    if (set.next()) {
                        LOG.error("Error while getting Ingredient by id. More than one record retrieved from database for id {}",id);
                        throw new ServiceFailureException("More than one record retrieved from database for id=" + id);
                    }
                    return rv;
                } else {
                    LOG.error("Error while getting Ingredient by id. Requested entity with id {} was not found!", id);
                    throw new EntityNotFoundException("Requested entity with id '" + id + "' was not found!");
                }
            }
        } catch (SQLException ex) {
            LOG.error("SQL Exception occured while getting Ingredient by id. Message of exception: {}", ex.getMessage());
            throw new ServiceFailureException("Error occured while retrieving ingredient with id " + id, ex);
        }
    }

    @Override
    public List<Ingredient> getAllIngredients() throws ServiceFailureException {
        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM Ingredient")) {

            return parseRows(statement);

        } catch (SQLException ex) {
            LOG.error("SQL Exception occured while getting all Ingredients. Message of exception: {}", ex.getMessage());
            throw new ServiceFailureException("Error occured while retrieving all ingredients", ex);
        }
    }

    @Override
    public List<Ingredient> searchByName(String name) throws ServiceFailureException {
        if (name == null) {
            LOG.error("Error while searching Ingredient by name. Name is null.");
            throw new IllegalArgumentException("name can't be null!");
        }

        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM Ingredient WHERE LOWERNAME LIKE ?")) {
            String qName = "%" + name.toLowerCase() + "%";
            statement.setString(1, qName);

            return parseRows(statement);

        } catch (SQLException ex) {
            LOG.error("SQL Exception occured while searching Ingredients by name. Message of exception: {}", ex.getMessage());
            throw new ServiceFailureException("Error occured while retrieving ingredients", ex);
        }
    }

    /**
     * Executes given <b>PreparedStatement</b> and parses acquired
     * <b>ResultSet</b> to list of <b>Ingredient</b>s
     *
     * @param statement Statement to be executed
     * @return List of Ingredients parsed from ResultSet of given statement
     * @throws SQLException
     */
    private List<Ingredient> parseRows(final PreparedStatement statement) throws SQLException {
        try (ResultSet set = statement.executeQuery()) {
            ArrayList<Ingredient> items = new ArrayList<>();
            while (set.next()) {
                items.add(fromResultSet(set));
            }
            return items;
        }
    }

    /**
     * Parses one Ingredient object from ResultSet on actual cursor position
     *
     * @param set ResultSet to load data from
     * @return Parsed Ingredient object
     * @throws SQLException
     */
    private static Ingredient fromResultSet(ResultSet set) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(set.getLong("ID"));
        ingredient.setName(set.getString("NAME"));

        return ingredient;
    }

    /**
     * Validates given ingredient - tests for null values of object it self,
     * name attribute and an id. Name is also tested for empty value.
     * <b>In case of validation fail, IllegalArgumentException is thrown.</b>
     *
     * @param ingredient ingredient object to be tested
     * @throws IllegalArgumentException
     */
    private void validate(Ingredient ingredient) {
        validate(ingredient, false);
    }

    /**
     * Validates given ingredient - tests for null values of object it self,
     * name attribute and optionally an id. Name is also tested for empty value.
     * <b>In case of validation fail, IllegalArgumentException is thrown.</b>
     *
     * @param ingredient ingredient object to be tested
     * @param allowNullIdentity true - ingredient id wont be tested for null
     * value | false - ingredient id will be tested for null value
     * @throws IllegalArgumentException
     */
    private void validate(Ingredient ingredient, boolean allowNullIdentity) throws IllegalArgumentException {
        LOG.debug("Validating object Ingredient.");
        
        if (ingredient == null) {
            LOG.error("Object not valid. Object Ingredient was null.");
            throw new IllegalArgumentException("Null ingredient entity supplied!");
        }
        if (ingredient.getName() == null) {
            LOG.error("Object not valid. Object Ingredient has null name.");
            throw new IllegalArgumentException("Name of ingredient can't be null!");
        }
        if (ingredient.getName().isEmpty()) {
            LOG.error("Object not valid. Object Ingredient has empty name.");
            throw new IllegalArgumentException("Name of ingredient can't be empty!");
        }
        if ((!allowNullIdentity) && (ingredient.getId() == null)) {
            LOG.error("Object not valid. Object Ingredient has null id.");
            throw new IllegalArgumentException("Id of ingredient can't be null!");
        }
    }

}
