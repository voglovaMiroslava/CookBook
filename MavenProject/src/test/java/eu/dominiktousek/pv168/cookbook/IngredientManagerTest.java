package eu.dominiktousek.pv168.cookbook;

import eu.dominiktousek.pv168.cookbook.daocontext.DBDataSourceFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;

import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test class responsible for testing IngredientManager functionality
 * 
 * @author Dominik Tousek (422385)
 */
public class IngredientManagerTest {
    
    private IngredientManager manager;
    private static DataSource dataSource;
    private static final DBUtilDerbyImpl dbKeeper = new DBUtilDerbyImpl();
  
    
    @BeforeClass
    public static void setUpClass(){
        dataSource = DBDataSourceFactory.getDataSource();
        dbKeeper.prepareDatabase();
    }
    
    @AfterClass
    public static void cleanUpClass(){
        dbKeeper.clearDatabase();
    }
    
    @Before
    public void setUp() throws SQLException { 
        manager = new IngredientManagerImpl(dataSource);
        dbKeeper.clearDatabase();
    }
    
    //Creation tests
    @Test
    public void createIngredientAllGood(){
        Ingredient ingredient = newIngredient(null, "Strouhanka");
        
        manager.createIngredient(ingredient);
        assertThat("No id assigned during create method",ingredient.getId(),is(not(equalTo(null))));
        
        Ingredient newIng = manager.getIngredientById(ingredient.getId());
        assertThat("Record not found after create",newIng,is(not(equalTo(null))));
        
        assertThat("Stored record is not equal to object in memory",newIng,is(equalTo(ingredient)));
        assertThat("Object retrieved from storage is same instance as object in memory",newIng,is(not(sameInstance(ingredient))));
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createIngredientWithNullName(){
        Ingredient ingredient = newIngredient(null, null);
        manager.createIngredient(ingredient);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createIngredientWithEmptyName(){
        Ingredient ingredient = newIngredient(null, "");
        manager.createIngredient(ingredient);
    }
    
    @Test
    public void createIngredientWithNonEmptyName(){
        Ingredient ingredient = newIngredient(null, "Cibule");
        
        manager.createIngredient(ingredient);
        
        Long id = ingredient.getId();
        
        assertThat("Ingredient id after creation can't be null.", id, is(not(equalTo(null))));
        assertThat("Ingredient name changed.",ingredient.getName(),is(equalTo("Cibule")));
    }
    
    //Retrieve tests
    @Test(expected = IllegalArgumentException.class)
    public void retrieveWithNullId(){
        manager.getIngredientById(null);
    }
    
    @Test
    public void retrieveGood(){
        Ingredient ingredient = newIngredient(null, "cibule");
        
        manager.createIngredient(ingredient);
        
        Ingredient dbValue = manager.getIngredientById(ingredient.getId());
        
        assertThat(dbValue,is(equalTo(ingredient)));
        
        assertThat(dbValue,is(not(sameInstance(ingredient))));
    }
    
    @Test
    public void retrieveNonExistentFromNotEmptyDB(){
        Ingredient ingredient = newIngredient(null, "cibule");
        
        manager.createIngredient(ingredient);
        
        Ingredient dbValue = manager.getIngredientById(ingredient.getId()+20l);
        
        assertThat(dbValue,is(not(equalTo(ingredient))));
        
        assertThat(dbValue,is(not(sameInstance(ingredient))));
    }
    
    @Test
    public void retrieveNonExistentFromEmptyDB(){        
        Ingredient dbValue = manager.getIngredientById(20l);
        
        assertThat(dbValue,is(equalTo(null)));
    }
    
    @Test
    public void getAllIngredientsEmptyDB(){
        List<Ingredient> allIngredients = manager.getAllIngredients();
        
        assertThat(allIngredients.size(),is(equalTo(0)));
    }
    
    @Test
    public void getAllIngredientsNonEmptyDB(){
        List<Ingredient> ingredients = Arrays.asList(
                newIngredient(null, "Cibule1"),
                newIngredient(null, "Cibule2"),
                newIngredient(null, "Cibule3"),
                newIngredient(null, "Cibule4"),
                newIngredient(null, "Cibule5"),
                newIngredient(null, "Cibule6")
        );
        
        for(Ingredient ing:ingredients){
            manager.createIngredient(ing);
        }
        
        List<Ingredient> dbValues = manager.getAllIngredients();
        
        ingredients.sort(idComparator);
        dbValues.sort(idComparator);
        
        assertThat(dbValues,is(equalTo(ingredients)));
        
    }
    
    //Update tests
    @Test
    public void UpdateGood(){
        Ingredient ingredient = newIngredient(null, "Cibule");
        
        manager.createIngredient(ingredient);
        
        ingredient.setName("NotCibule");
        
        manager.updateIngredient(ingredient);
        
        Ingredient dbValue = manager.getIngredientById(ingredient.getId());
        
        assertThat(dbValue,is(equalTo(ingredient)));
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void UpdateNonExistentOnEmptyDB(){
        Ingredient ingredient = newIngredient(10l, "Cibule");
              
        manager.updateIngredient(ingredient);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void UpdateNonExistentOnNonEmptyDB(){
        Ingredient ingredient = newIngredient(null, "Cibule");
        
        manager.createIngredient(ingredient);
        
        Ingredient newVal = newIngredient(ingredient.getId()+10l, "Strounhanka");
        
        manager.updateIngredient(newVal);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void UpdateWithNullId(){
        Ingredient ingredient = newIngredient(null, "Cibule");
              
        manager.updateIngredient(ingredient);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void UpdateWithNullName(){
        Ingredient ingredient = newIngredient(10l, null);
              
        manager.updateIngredient(ingredient);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void UpdateWithEmptyName(){
        Ingredient ingredient = newIngredient(10l, "");
              
        manager.updateIngredient(ingredient);
    }
    
    //Search tests
    @Test
    public void searchCorrectAllGoodMatchAll(){
        List<Ingredient> ingredients = Arrays.asList(
                newIngredient(null, "Cibule1"),
                newIngredient(null, "Cibule2"),
                newIngredient(null, "Cibule3"),
                newIngredient(null, "Cibule4"),
                newIngredient(null, "Cibule5"),
                newIngredient(null, "Cibule6")
        );
        
        for(Ingredient i:ingredients){
            manager.createIngredient(i);
        }
        
        List<Ingredient> searchByName = manager.searchByName("Cibule1");
        assertThat(searchByName.size(),is(equalTo(1)));
        assertThat(searchByName.get(0),is(equalTo(ingredients.get(0))));
                
        searchByName = manager.searchByName("Cibule");
        assertThat(searchByName.size(),is(equalTo(6)));
        ingredients.sort(idComparator);
        searchByName.sort(idComparator);
        assertThat(searchByName,is(equalTo(ingredients)));
    }
    
    public void searchCorrectAllGoodMatchNotAll(){
        List<Ingredient> ingredients = new ArrayList(Arrays.asList(
                newIngredient(null, "Cibule1"),
                newIngredient(null, "Cibule2"),
                newIngredient(null, "Pistole"),
                newIngredient(null, "Cibule4"),
                newIngredient(null, "Kytara"),
                newIngredient(null, "Cibule6")
        ));
        
        for(Ingredient i:ingredients){
            manager.createIngredient(i);
        }
        ingredients.removeIf(
                (Ingredient i) -> !i.getName().startsWith("Cibule")
        );
        
        List<Ingredient> searchByName = manager.searchByName("Cibule");
        assertThat(searchByName.size(),is(equalTo(6)));
        ingredients.sort(idComparator);
        searchByName.sort(idComparator);
        assertThat(searchByName,is(equalTo(ingredients)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void searchWithNullName(){
        manager.searchByName(null);
    }
    
    @Test
    public void searchWithEmptyNameOnNonEmptyDB(){
        List<Ingredient> ingredients = Arrays.asList(
                newIngredient(null, "Cibule1"),
                newIngredient(null, "Cibule2"),
                newIngredient(null, "Cibule3"),
                newIngredient(null, "Cibule4"),
                newIngredient(null, "Cibule5"),
                newIngredient(null, "Cibule6")
        );
        
        for(Ingredient i:ingredients){
            manager.createIngredient(i);
        }
                        
        List<Ingredient> searchByName = manager.searchByName("");
        assertThat(searchByName.size(),is(equalTo(6)));
        ingredients.sort(idComparator);
        searchByName.sort(idComparator);
        assertThat(searchByName,is(equalTo(ingredients)));
    }
    
    @Test
    public void searchWithUtf8String(){
        List<Ingredient> ingredients = new ArrayList(Arrays.asList(
                newIngredient(null, "Cibule1"),
                newIngredient(null, "Cibule2"),
                newIngredient(null, "Čibule3"),
                newIngredient(null, "Cibule4"),
                newIngredient(null, "Cibule5"),
                newIngredient(null, "Cibule6")
        ));
        
        for(Ingredient i:ingredients){
            manager.createIngredient(i);
        }
        
        ingredients.removeIf(
                (Ingredient i) -> i.getName().startsWith("Cibule")
        );
                        
        List<Ingredient> searchByName = manager.searchByName("Čibule");
        assertThat(searchByName.size(),is(equalTo(1)));
        ingredients.sort(idComparator);
        searchByName.sort(idComparator);
        assertThat(searchByName,is(equalTo(ingredients)));
    }
    
    //Deletition tests
    @Test
    public void removeExistingIngredient(){
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Cibule");
        
        manager.createIngredient(ingredient);
        
        Long id = ingredient.getId();
        
        assertThat("Ingredient id after creation can't be null.", id, is(not(equalTo(null))));
        assertThat("Ingredient name changed.",ingredient.getName(),is(equalTo("Cibule")));
        
        manager.removeIngredient(ingredient);
        
        Ingredient nonExistentIngredient = manager.getIngredientById(id);
        
        assertThat("Ingredient exists after removing",nonExistentIngredient,is(equalTo(null)));
    }
    
    @Test
    public void removeNonExistentIngredient(){        
        Ingredient ing1 = new Ingredient();
        ing1.setName("Šiška");
        
        Ingredient ing2 = new Ingredient();
        ing2.setName("Kočička");
        
        Ingredient ing3 = new Ingredient();
        ing3.setName("Žábí nožička");
        
        manager.createIngredient(ing1);
        manager.createIngredient(ing2);
        manager.createIngredient(ing3);
        
        List<Ingredient> all = manager.getAllIngredients();
        
        Ingredient nonExistent = new Ingredient();
        nonExistent.setName("Cibule");
        nonExistent.setId(Long.max(ing1.getId(), Long.max(ing2.getId(), ing3.getId()))+1);
        
        try{
            manager.removeIngredient(nonExistent);
        }catch(EntityNotFoundException ex){
            //ok
        }
        
        List<Ingredient> allAfterOp = manager.getAllIngredients();
        
        all.sort(idComparator);
        allAfterOp.sort(idComparator);
        
        assertThat("Attempt to remove non-existing record from storage caused data loss",all,is(equalTo(allAfterOp)));
    }
    
    @Test
    public void removeIngredientWithNullId(){      
        Ingredient ing1 = new Ingredient();
        ing1.setName("Šiška");
        
        Ingredient ing2 = new Ingredient();
        ing2.setName("Kočička");
        
        Ingredient ing3 = new Ingredient();
        ing3.setName("Žábí nožička");
        
        manager.createIngredient(ing1);
        manager.createIngredient(ing2);
        manager.createIngredient(ing3);
        
        List<Ingredient> all = manager.getAllIngredients();
        
        Ingredient nonExistent = new Ingredient();
        nonExistent.setName("Cibule");
        nonExistent.setId(null);

        try{
            manager.removeIngredient(nonExistent);
            fail();
        }catch(IllegalArgumentException ex){
            //ok
        }
        
        List<Ingredient> allAfterOp = manager.getAllIngredients();
        
        all.sort(idComparator);
        allAfterOp.sort(idComparator);
        
        assertThat("Attempt to remove non-existing record from storage caused data loss",all,is(equalTo(allAfterOp)));
    }
    
    private static final Comparator<Ingredient> idComparator = new Comparator<Ingredient>() {
        @Override
        public int compare(Ingredient o1, Ingredient o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
    
    private static Ingredient newIngredient(Long id, String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        
        return ingredient;
    }
}
