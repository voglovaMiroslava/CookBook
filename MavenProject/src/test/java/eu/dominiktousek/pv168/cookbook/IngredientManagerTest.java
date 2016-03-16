/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dominiktousek.pv168.cookbook;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Dominik
 */
public class IngredientManagerTest {
    
    private IngredientManager manager;
    
    
    @Before
    public void setUp() throws SQLException {
        manager = new IngredientManagerImpl();
        clearAllData();
    }
    
    @After
    public void cleanUp(){
        clearAllData();
    }
    
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
        assertThat("Ingredient name changed.",ingredient.getName(),is("Cibule"));
    }
    
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
        List<Ingredient> all = manager.getAllIngredients();
        assertThat("Storage must be empty at the test start.",all.size(),is(equalTo(0)));
        
        Ingredient ing1 = new Ingredient();
        ing1.setName("Šiška");
        
        Ingredient ing2 = new Ingredient();
        ing2.setName("Kočička");
        
        Ingredient ing3 = new Ingredient();
        ing3.setName("Žábí nožička");
        
        manager.createIngredient(ing1);
        assertThat("No id assigned during create method",ing1.getId(),is(not(equalTo(null))));
        manager.createIngredient(ing2);
        assertThat("No id assigned during create method",ing2.getId(),is(not(equalTo(null))));
        manager.createIngredient(ing3);
        assertThat("No id assigned during create method",ing3.getId(),is(not(equalTo(null))));
        
        Ingredient nonExistent = new Ingredient();
        nonExistent.setName("Cibule");
        nonExistent.setId(Long.max(ing1.getId(), Long.max(ing2.getId(), ing3.getId()))+1);

        manager.removeIngredient(nonExistent);
        
        List<Ingredient> allAfterOp = manager.getAllIngredients();
        
        assertThat("Creating new records in storage had no effect",allAfterOp.isEmpty(),is(equalTo(true)));
        
        all.sort(idComparator);
        allAfterOp.sort(idComparator);
        
        assertThat("Attempt to remove non-existing record from storage caused data loss",all,is(equalTo(allAfterOp)));
    }
    
    @Test
    public void removeIngredientWithNullId(){
        List<Ingredient> all = manager.getAllIngredients();
        assertThat("Storage must be empty at the test start.",all.size(),is(equalTo(0)));
        
        Ingredient ing1 = new Ingredient();
        ing1.setName("Šiška");
        
        Ingredient ing2 = new Ingredient();
        ing2.setName("Kočička");
        
        Ingredient ing3 = new Ingredient();
        ing3.setName("Žábí nožička");
        
        manager.createIngredient(ing1);
        assertThat("No id assigned during create method",ing1.getId(),is(not(equalTo(null))));
        manager.createIngredient(ing2);
        assertThat("No id assigned during create method",ing2.getId(),is(not(equalTo(null))));
        manager.createIngredient(ing3);
        assertThat("No id assigned during create method",ing3.getId(),is(not(equalTo(null))));
        
        
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
    
    
    private void clearAllData(){
        List<Ingredient> all = manager.getAllIngredients();
        for (Iterator<Ingredient> it = all.iterator(); it.hasNext();) {
            Ingredient i = it.next();
            manager.removeIngredient(i);
        }
    }
    
    private static final Comparator<Ingredient> idComparator = new Comparator<Ingredient>() {
        @Override
        public int compare(Ingredient o1, Ingredient o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
}
