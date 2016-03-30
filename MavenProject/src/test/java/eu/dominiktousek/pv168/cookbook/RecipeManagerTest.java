
package eu.dominiktousek.pv168.cookbook;

import org.junit.*;

import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Miroslava Voglova
 */
public class RecipeManagerTest {
    
    private RecipeManager manager;
    private static final DBUtilDerbyImpl dbKeeper = new DBUtilDerbyImpl();
    
    @BeforeClass
    public static void setUpClass(){
        dbKeeper.prepareDatabase();
    }
    
    @AfterClass
    public static void cleanUpClass(){
        dbKeeper.clearDatabase();
    }
    
    @Before
    public void init() throws SQLException {
        manager = new RecipeManagerImpl();
        dbKeeper.clearDatabase();
    }

    @Test
    public void createRecipeAllGood() {
        Recipe rec = createRecipe("Bramboracka", "Boil water, add potatoes", Duration.ofHours(5L));
        manager.createRecipe(rec);

        assertThat(rec.getId(), is(not(equalTo(null))));
        assertThat(rec.getName(), is(equalTo("Bramboracka")));
        assertThat(rec.getInstructions(), is(equalTo("Boil water, add potatoes")));
        assertThat(rec.getDuration(), is(equalTo(Duration.ofHours(5L))));
    }

    @Test
    public void createRecipeWithoutDuration() {
        Recipe rec = createRecipe("Bramboracka", "Boil water, add potatoes", null);
        manager.createRecipe(rec);

        assertThat(rec.getId(), is(not(equalTo(null))));
        assertThat(rec.getName(), is(equalTo("Bramboracka")));
        assertThat(rec.getInstructions(), is(equalTo("Boil water, add potatoes")));
        assertThat(rec.getDuration(), is(equalTo(null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRecipeNullName() {
        Recipe rec = createRecipe(null, "instructions", null);
        manager.createRecipe(rec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRecipeEmptyName() {
        Recipe rec = createRecipe("", "instructions", null);
        manager.createRecipe(rec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRecipeNullInstruction() {
        Recipe rec = createRecipe("name", null, null);
        manager.createRecipe(rec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRecipeEmptyInstruction() {
        Recipe rec = createRecipe("name", "", null);
        manager.createRecipe(rec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithExistingId() {
        Recipe rec = createRecipe("fancy name", "awesome instructions", Duration.ofMinutes(41L));
        rec.setId(7L);
        manager.createRecipe(rec);
    }

    @Test
    public void updateRecipeGood() {
        Recipe rec = createRecipe("name", "instructions", Duration.ofHours(2L));
        manager.createRecipe(rec);

        rec.setDuration(Duration.ofMinutes(54L));
        manager.updateRecipe(rec);
        Recipe other = manager.getRecipeById(rec.getId());
        assertTrue("After duration update one or more of the attributes does not match.", checkAttributes(rec, other));

        rec.setInstructions("other, more useful instructions");
        manager.updateRecipe(rec);
        other = manager.getRecipeById(rec.getId());
        assertTrue("After recipe update one or more of the attributes does not match.", checkAttributes(rec, other));

        rec.setName("fancy new name");
        manager.updateRecipe(rec);
        other = manager.getRecipeById(rec.getId());
        assertTrue("After recipe update one or more of the attributes does not match.", checkAttributes(rec, other));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRecipeNullName() {
        Recipe rec = createRecipe("name", "instructions", Duration.ofHours(2L));
        manager.createRecipe(rec);
        rec.setName(null);
        manager.updateRecipe(rec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRecipeEmptyName() {
        Recipe rec = createRecipe("name", "instructions", Duration.ofHours(2L));
        manager.createRecipe(rec);
        rec.setName("");
        manager.updateRecipe(rec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRecipeNullInstruction() {
        Recipe rec = createRecipe("name", "instructions", Duration.ofHours(2L));
        manager.createRecipe(rec);
        rec.setInstructions(null);
        manager.updateRecipe(rec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRecipeEmptyInstruction() {
        Recipe rec = createRecipe("name", "instructions", Duration.ofHours(2L));
        manager.createRecipe(rec);
        rec.setInstructions("");
        manager.updateRecipe(rec);
    }

    @Test
    public void returnsAllRecipes() {
        List<Recipe> controlValues = fullDatabase();
        List<Recipe> valuesFromDatabase = manager.getAllRecipes();

        controlValues.sort(idComparator);
        valuesFromDatabase.sort(idComparator);

        assertTrue(controlValues.equals(valuesFromDatabase));
    }

    @Test
    public void searchRecipeById() {
        Recipe rec = createRecipe("name", "ins", null);
        manager.createRecipe(rec);
        Recipe other = manager.getRecipeById(rec.getId());
        assertTrue("GetRecipeById did not return same recipe.", checkAttributes(rec, other));
    }

    @Test
    public void durationSearchBothDurationsSet() {
        List<Recipe> controlValues = fullDatabase();
        List<Recipe> valuesFromManager = manager.searchByDuration(Duration.ofMinutes(15L), Duration.ofHours(2L));
        controlValues.removeIf((Recipe rec) -> rec.getDuration() == null
                || rec.getDuration().compareTo(Duration.ofMinutes(15L))<=0
                || rec.getDuration().compareTo(Duration.ofHours(2L))>=0);

        controlValues.sort(idComparator);
        valuesFromManager.sort(idComparator);
        assertTrue(controlValues.equals(valuesFromManager));
    }

    @Test
    public void durationSearchBottomDurationSet() {
        List<Recipe> controlValues = fullDatabase();
        List<Recipe> valuesFromManager = manager.searchByDuration(Duration.ofMinutes(15L), null);
        controlValues.removeIf((Recipe rec) -> rec.getDuration() == null
                || rec.getDuration().compareTo(Duration.ofMinutes(15L))<=0);

        controlValues.sort(idComparator);
        valuesFromManager.sort(idComparator);
        assertTrue(controlValues.equals(valuesFromManager));
    }

    @Test
    public void durationSearchUpDurationSet() {
        List<Recipe> controlValues = fullDatabase();
        List<Recipe> valuesFromManager = manager.searchByDuration(null, Duration.ofHours(2L));
        controlValues.removeIf((Recipe rec) -> rec.getDuration() == null
                || rec.getDuration().compareTo(Duration.ofHours(2L))>=0);

        controlValues.sort(idComparator);
        valuesFromManager.sort(idComparator);
        assertTrue(controlValues.equals(valuesFromManager));
    }


    @Test
    public void durationSearchNoneDurationsSet() {
        List<Recipe> controlValues = fullDatabase();
        List<Recipe> valuesFromManager = manager.searchByDuration(null, null);

        controlValues.sort(idComparator);
        valuesFromManager.sort(idComparator);
        assertTrue("When no duration boundary is set, all values should be returned.", controlValues.equals(valuesFromManager));
    }

    @Test
    public void searchByName() {
        List<Recipe> controlValues = fullDatabase();
        List<Recipe> valuesFromManager = manager.searchByName("na");
        controlValues.removeIf((Recipe rec) ->
                !(Pattern.compile(Pattern.quote("na"), Pattern.CASE_INSENSITIVE).matcher(rec.getName()).find()));

        controlValues.sort(idComparator);
        valuesFromManager.sort(idComparator);
        assertTrue(controlValues.equals(valuesFromManager));
    }

    @Test
    public void searchByEmptyName() {
        List<Recipe> controlValues = fullDatabase();
        List<Recipe> valuesFromManager = manager.searchByName("");

        controlValues.sort(idComparator);
        valuesFromManager.sort(idComparator);
        assertTrue("Should return all records.", controlValues.equals(valuesFromManager));
    }

    @Test(expected = IllegalArgumentException.class)
    public void searchByNullName() {
        List<Recipe> controlValues = fullDatabase();
        List<Recipe> valuesFromManager = manager.searchByName(null);
    }

    private List<Recipe> fullDatabase() {
        Recipe rec1 = createRecipe("name", "instruction", Duration.ofMinutes(87L));
        Recipe rec2 = createRecipe("PotatoesSoup", "water and potatoes mixed together", Duration.ofHours(2L));
        Recipe rec3 = createRecipe("TunnaFried", "AI! tunna tunna", Duration.ofMinutes(45L));
        Recipe rec4 = createRecipe("LovePotion", "frog legs and more", Duration.ofDays(8L));
        Recipe rec5 = createRecipe("Dont eat this", "to much expensive to do", null);

        List<Recipe> allRecipes = new ArrayList<>(Arrays.asList(rec1, rec2, rec3, rec4, rec5));
        for (Recipe recipe : allRecipes) {
            manager.createRecipe(recipe);
        }

        return allRecipes;
    }

    private static boolean checkAttributes(Recipe one, Recipe other) {
        if (one == null && other == null) {
            return true;
        }

        if (one == null || other == null) {
            return false;
        }

        boolean equality = true;

        equality = equality && checkNullAndEquality(one.getId(), other.getId());
        equality = equality && checkNullAndEquality(one.getDuration(), other.getDuration());
        equality = equality && checkNullAndEquality(one.getInstructions(), other.getInstructions());
        equality = equality && checkNullAndEquality(one.getName(), other.getName());

        return equality;
    }

    private static boolean checkNullAndEquality(Object one, Object other) {
        if (one != null) {
            return one.equals(other);
        }

        return other == null;
    }

    private static Recipe createRecipe(String name, String instruction, Duration duration) {
        Recipe rec = new Recipe();

        rec.setName(name);
        rec.setInstructions(instruction);
        rec.setDuration(duration);

        return rec;
    }

    private static final Comparator<Recipe> idComparator = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe o1, Recipe o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
}
