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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of manager responsible for CRUD operations on Recipe. Based on
 * SQL database storage
 *
 * @author Dominik Tousek (422385) & Miroslava Voglova (382579)
 */
public class RecipeManagerImpl implements RecipeManager {

    private final DataSource dataSource;
    final static Logger LOG = LoggerFactory.getLogger(RecipeManagerImpl.class);

    public RecipeManagerImpl() {
        this(DBDataSourceFactory.getDataSource());
    }

    public RecipeManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createRecipe(Recipe recipe) {
        validate(recipe, true);

        LOG.debug("Creating new Recipe.");

        if (recipe.getId() != null) {
            LOG.error("Error while creating Recipe. Id of recipe was already set!");
            throw new IllegalArgumentException("Id of recipe was already set!");
        }

        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Recipe (NAME,INSTRUCTIONS,DURATION) VALUES(?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, recipe.getName());
            statement.setString(2, recipe.getInstructions());
            if (recipe.getDuration() != null) {
                statement.setLong(3, recipe.getDuration().toMinutes());
            } else {
                statement.setNull(3, java.sql.Types.BIGINT);
            }
            statement.executeUpdate();

            try (ResultSet set = statement.getGeneratedKeys()) {
                if (!set.next()) {
                    LOG.error("Error while creating Recipe. Recipe was not created.");
                    throw new ServiceFailureException("No generated key retrieved from database!");
                }
                Long id = set.getLong(1);
                if (set.next()) {
                    LOG.error("Error while creating Recipe. More than one Recipe created.");
                    throw new ServiceFailureException("More than one record in database affected during one CREATE!");
                }
                recipe.setId(id);
            }

        } catch (SQLException ex) {
            LOG.error("SQL Exception occured while creating Recipe. Message of exception: {}", ex.getMessage());
            throw new ServiceFailureException("Error occured while creating new recipe '" + recipe + "'", ex);
        }
    }

    @Override
    public void updateRecipe(Recipe recipe) {
        validate(recipe);

        LOG.debug("Updating new Recipe.");

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE RECIPE SET name=?, instructions=?, duration=? WHERE id = ?")) {

            statement.setString(1, recipe.getName());
            statement.setString(2, recipe.getInstructions());
            if (recipe.getDuration() != null) {
                statement.setLong(3, recipe.getDuration().toMinutes());
            } else {
                statement.setNull(3, java.sql.Types.BIGINT);
            }
            statement.setLong(4, recipe.getId());

            int count = statement.executeUpdate();
            if (count == 0) {
                LOG.error("Error while updating Recipe. Recipe to update was not found.");
                throw new EntityNotFoundException("Recipe to update: " + recipe + "was not found.");
            }
            if (count > 1) {
                LOG.error("Error while updating Recipe. More than one Recipe updated.");
                throw new ServiceFailureException("More than one ( " + count + " ) recipe updated!");
            }

        } catch (SQLException e) {
            LOG.error("SQL Exception occured while updating Recipe. Message of exception: {}", e.getMessage());
            throw new ServiceFailureException("Error occurred while updating recipe: " + recipe, e);
        }

    }

    @Override
    public void deleteRecipe(Long id) {
        if (id == null) {
            LOG.error("Error while deleting Recipe. Recipe id null.");
            throw new IllegalArgumentException("Id can't be null!");
        }

        LOG.debug("Deleting Recipe.");

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM RECIPE WHERE id = ?")) {

            statement.setLong(1, id);
            int count = statement.executeUpdate();
            if (count == 0) {
                LOG.error("Error while deleting Recipe. Recipe to delete was not found.");
                throw new EntityNotFoundException("Recipe to delete with id: " + id + "was not found.");
            }
            if (count > 1) {
                LOG.error("Error while deleting Recipe. Recipe to delete was not found.");
                throw new ServiceFailureException("More than one ( " + count + " ) recipe deleted!");
            }
        } catch (SQLException e) {
            LOG.error("SQL Exception occured while deleting Recipe. Message of exception: {}", e.getMessage());
            throw new ServiceFailureException("Error occurred while removing recipe with id " + id, e);
        }
    }

    @Override
    public Recipe getRecipeById(Long id) {
        if (id == null) {
            LOG.error("Error while getting Recipe by id. Id is null.");
            throw new IllegalArgumentException("Argument id can't be null!");
        }

        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM Recipe WHERE ID=?")) {

            statement.setLong(1, id);

            statement.execute();

            try (ResultSet set = statement.getResultSet()) {
                if (set.next()) {
                    Recipe found = fromResultSet(set);

                    if (set.next()) {
                        LOG.error("Error while getting Recipe by id. More than one record retrieved from database for id {}", id);
                        throw new ServiceFailureException(
                                "More than one record retrieved from database for id=" + id);
                    }

                    return found;
                } else {
                    LOG.error("Error while getting Recipe by id. Entity with id {} was not found", id);
                    throw new EntityNotFoundException("Entity with id:" + id + " was not found");
                }
            }

        } catch (SQLException ex) {
            LOG.error("SQL Exception occured while getting Recipe by id {}. Message of exception: {}", id, ex.getMessage());
            throw new ServiceFailureException("Error occured while retrieving recipe with id " + id, ex);
        }
    }

    @Override
    public List<Recipe> getAllRecipes() {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM RECIPE")) {

            try (ResultSet rs = ps.executeQuery()) {
                List<Recipe> results = new ArrayList<>();

                while (rs.next()) {
                    results.add(fromResultSet(rs));
                }

                return results;
            }
        } catch (SQLException e) {
            LOG.error("SQL Exception occured while getting all Recipes. Message of exception: {}", e.getMessage());
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
            LOG.error("error while searching recipe. Name can not be null (can be empty).");
            throw new IllegalArgumentException("Name can't be null!");
        }

        if (!name.isEmpty()) {
            name = "%" + name.toLowerCase() + "%";
        }

        StringBuilder builder = prepareSearchQuery(name, durationFrom, durationTo, ingredients);
        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(builder.toString())) {

            if (!name.isEmpty()) {
                statement.setString(1, name);
            }
            return parseRows(statement);

        } catch (SQLException ex) {
            LOG.error("SQL Exception occured while searching Recipes. Message of exception: {}", ex.getMessage());
            throw new ServiceFailureException("Error occured while searching recipes", ex);
        }
    }

    /**
     * Prepares SQL query for search method
     *
     * @param name Name of recipe
     * @param durationFrom Minimal value of duration
     * @param durationTo Maximal value of duration
     * @param ingredients List of ingredients in recipe
     * @return StringBuilder with prepared query
     */
    private StringBuilder prepareSearchQuery(String name, Duration durationFrom, Duration durationTo, List<Ingredient> ingredients) {
        StringBuilder builder = new StringBuilder(60);
        builder.append("SELECT * FROM Recipe WHERE 1=1");
        if (!name.isEmpty()) {
            builder.append(" AND LOWER(NAME) LIKE ?");
        }
        if (durationFrom != null) {
            builder.append(" AND DURATION > ");
            builder.append(durationFrom.toMinutes());
        }
        if (durationTo != null) {
            builder.append(" AND DURATION < ");
            builder.append(durationTo.toMinutes());
        }
        if (ingredients != null && !ingredients.isEmpty()) {
            builder.append(" AND ID IN (SELECT RECIPEID FROM INGREDIENTAMOUNT WHERE");

            boolean first = true;
            for (Ingredient item : ingredients) {
                if (!first) {
                    builder.append(" AND ");
                } else {
                    first = false;
                }
                builder.append("ID=");
                builder.append(item.getId().toString());
            }

            builder.append(")");
        }
        return builder;
    }

    /**
     * Executes given <b>PreparedStatement</b> and parses acquired
     * <b>ResultSet</b> to list of <b>Recipe</b>s
     *
     * @param statement Statement to be executed
     * @return List of Recipes parsed from ResultSet of given statement
     * @throws SQLException
     */
    private static List<Recipe> parseRows(final PreparedStatement statement) throws SQLException {
        try (ResultSet set = statement.executeQuery()) {
            ArrayList<Recipe> items = new ArrayList<>();
            while (set.next()) {
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
    private static Recipe fromResultSet(ResultSet set) throws SQLException {
        Recipe recipe = new Recipe();
        recipe.setId(set.getLong("ID"));
        recipe.setName(set.getString("NAME"));
        recipe.setInstructions(set.getString("INSTRUCTIONS"));
        Long duration = set.getLong("DURATION");
        if (!set.wasNull()) {
            recipe.setDuration(Duration.ofMinutes(duration));
        }

        return recipe;
    }

    /**
     * Validates given recipe - tests for null values of object it self, name
     * attribute, instructions attribute and an id. Name and instructions are
     * also tested for empty value.
     * <b>In case of validation fail, IllegalArgumentException is thrown.</b>
     *
     * @param recipe recipe object to be tested
     * @throws IllegalArgumentException
     */
    private static void validate(Recipe recipe) {
        validate(recipe, false);
    }

    /**
     * Validates given recipe - tests for null values of object it self, name
     * attribute, instructions attribute and optionally an id. Name and
     * instructions are also tested for empty value.
     * <b>In case of validation fail, IllegalArgumentException is thrown.</b>
     *
     * @param recipe recipe object to be tested
     * @param allowNullIdentity true - recipe id wont be tested for null value |
     * false - recipe id will be tested for null value
     * @throws IllegalArgumentException
     */
    private static void validate(Recipe recipe, boolean allowNullIdentity) {
        /*
        id - optionally could be null
        name - can't be null or empty
        instructions - can't be null or empty
        duration - could be null
         */
        LOG.debug("Validating Recipe object.");
        if (recipe == null) {
            LOG.error("Object not valid. Object Recipe was null.");
            throw new IllegalArgumentException("Null recipe entity supplied!");
        }
        if (recipe.getName() == null) {
            LOG.error("Object not valid. Name is null.");
            throw new IllegalArgumentException("Name of recipe can't be null!");
        }
        if (recipe.getName().isEmpty()) {
            LOG.error("Object not valid. Name is empty.");
            throw new IllegalArgumentException("Name of recipe can't be empty!");
        }
        if (recipe.getInstructions() == null) {
            LOG.error("Object not valid. Instructions are null.");
            throw new IllegalArgumentException("Instructions of recipe can't be null!");
        }
        if (recipe.getInstructions().isEmpty()) {
            LOG.error("Object not valid. Instructions are empty.");
            throw new IllegalArgumentException("Instructions of recipe can't be empty!");
        }
        if (!allowNullIdentity && recipe.getId() == null) {
            LOG.error("Object not valid. Id of Recipe is null.");
            throw new IllegalArgumentException("Id of recipe can't be null!");
        }
    }

}
