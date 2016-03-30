package eu.dominiktousek.pv168.cookbook;

import eu.dominiktousek.pv168.cookbook.daocontext.DBDataSourceFactory;
import java.sql.SQLException;
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
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Strouhanka");
        
        manager.createIngredient(ingredient);
        assertThat("No id assigned during create method",ingredient.getId(),is(not(equalTo(null))));
        
        Ingredient newIng = manager.getIngredientById(ingredient.getId());
        assertThat("Record not found after create",newIng,is(not(equalTo(null))));
        
        assertThat("Stored record is not equal to object in memory",newIng,is(equalTo(ingredient)));
        assertThat("Object retrieved from storage is same instance as object in memory",newIng,is(not(sameInstance(ingredient))));
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createIngredientWithNullName(){
        Ingredient ingredient = new Ingredient();
        ingredient.setName(null);
        manager.createIngredient(ingredient);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createIngredientWithEmptyName(){
        Ingredient ingredient = new Ingredient();
        ingredient.setName("");
        manager.createIngredient(ingredient);
    }
    
    @Test
    public void createIngredientWithNonEmptyName(){
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Cibule");
        
        manager.createIngredient(ingredient);
        
        Long id = ingredient.getId();
        
        assertThat("Ingredient id after creation can't be null.", id, is(not(equalTo(null))));
        assertThat("Ingredient name changed.",ingredient.getName(),is(equalTo("Cibule")));
    }
    
    //Retrieve tests
    //TODO:
    
    
    //Update tests
    //TODO:
    
    //Search tests
    //TODO:
    
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
}
