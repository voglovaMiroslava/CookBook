package eu.dominiktousek.pv168.cookbook;

import eu.dominiktousek.pv168.cookbook.daocontext.DBDataSourceFactory;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test class responsible for testing IngredientAmountManager functionality
 * 
 * @author Dominik Tousek (422385)
 */
public class IngredientAmountManagerTest {
    private IngredientAmountManager manager;
    private IngredientManager iMan;
    private RecipeManager rMan;
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
        manager = new IngredientAmountManagerImpl(dataSource);
        iMan = new IngredientManagerImpl(dataSource);
        rMan = new RecipeManagerImpl(dataSource);
        dbKeeper.clearDatabase();
    }
        
    //Creation tests
    @Test(expected = IllegalArgumentException.class)
    public void addNullIngredientAmount(){
        manager.addIngredientInRecipe(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void addIngredientAmountWithNullRecipeId(){
        Ingredient ingr = newIngredient(null, "Cibule");
        iMan.createIngredient(ingr);
        
        IngredientAmount item = newItem(null, null, ingr, "1 Ks");
        manager.addIngredientInRecipe(item);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void addIngredientAmountWithNullIngredient(){
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
        
        IngredientAmount item = newItem(null, recipe.getId(), null, "1 Ks");
        manager.addIngredientInRecipe(item);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void addIngredientAmountWithNullIngredientId(){
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibulleeee");
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void addIngredientAmountWithNullAmount(){
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibulleeee");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, null);
        manager.addIngredientInRecipe(item);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void addIngredientAmountWithEmptyAmount(){
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibulleeee");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "");
        manager.addIngredientInRecipe(item);
    }
    
    @Test
    public void addIngredientAmountOK(){
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibulleeee");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        assertThat(item.getId(),is(not(equalTo(null))));
    }
    
    //Retrieve tests
    @Test
    public void getIngredientAmountByIdAmountOK(){
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibulleeee");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        assertThat(item.getId(),is(not(equalTo(null))));
        
        IngredientAmount dbValue = manager.getIngredientAmountById(item.getId());
        assertThat(dbValue,is(equalTo(item)));
    }
    
    @Test
    public void getIngredientAmountByIdNonExistentOnEmptyDB(){        
        try{
            IngredientAmount dbValue = manager.getIngredientAmountById(20l);
            fail();
        }catch(EntityNotFoundException ex){
            //OK
        }
    }
    
    @Test
    public void getIngredientAmountByIdNonExistentOnNonEmptyDB(){
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibulleeee");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        assertThat(item.getId(),is(not(equalTo(null))));
        
        try{
            IngredientAmount dbValue = manager.getIngredientAmountById(item.getId()+20l);
            fail();
        }catch(EntityNotFoundException ex){
            //OK
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getByRecipeWithNullRecipeId(){
        manager.getIngredientsByRecipe(null);
    }
    
    @Test
    public void getByRecipeNonExistentOnEmptyDB(){
        List<IngredientAmount> ingredientsByRecipe = manager.getIngredientsByRecipe(20l);
        assertThat(ingredientsByRecipe.size(),is(equalTo(0)));
    }
    
    @Test
    public void getByRecipeNonExistentOnNonEmptyDB(){
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibulleeee");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        
        List<IngredientAmount> ingredientsByRecipe = manager.getIngredientsByRecipe(item.getRecipeId()+20l);
        assertThat(ingredientsByRecipe.size(),is(equalTo(0)));
    }
    
    @Test
    public void getByRecipeOneRecordOK(){
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibulleeee");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        
        List<IngredientAmount> ingredientsByRecipe = manager.getIngredientsByRecipe(item.getRecipeId());
        assertThat(ingredientsByRecipe.size(),is(equalTo(1)));
        assertThat(ingredientsByRecipe.get(0),is(equalTo(item)));
    }
    
    @Test
    public void getByRecipeMultipleRecordsOK(){
        List<IngredientAmount> items = new LinkedList<>();
        
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
            
        for(int i=0;i<20;i++){
            Ingredient ingredient = newIngredient(null, "Cibulleeee"+i);
            iMan.createIngredient(ingredient);

            IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
            manager.addIngredientInRecipe(item);
            
            items.add(item);
        }
        
        List<IngredientAmount> ingredientsByRecipe = manager.getIngredientsByRecipe(recipe.getId());
        items.sort(idComparator);
        ingredientsByRecipe.sort(idComparator);
        assertThat(ingredientsByRecipe.size(),is(equalTo(items.size())));
        assertThat(ingredientsByRecipe,is(equalTo(items)));
    }
    
    @Test
    public void getByRecipeMultipleRecordsNotAllMatchOK(){
        List<IngredientAmount> items = new LinkedList<>();
        
        Recipe recipe = newRecipe(null, "Nudle s mákem", "Not tellin ya!", Duration.ofMinutes(30l));
        rMan.createRecipe(recipe);
            
        for(int i=0;i<10;i++){
            Ingredient ingredient = newIngredient(null, "Cibulleeee"+i);
            iMan.createIngredient(ingredient);

            IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
            manager.addIngredientInRecipe(item);
            
            items.add(item);
        }
        
        Recipe recipe2 = newRecipe(null, "Nudle s mákem2", "Not tellin ya :)!", Duration.ofMinutes(20l));
        rMan.createRecipe(recipe2);
            
        for(int i=0;i<10;i++){
            Ingredient ingredient = newIngredient(null, "Cibulleeee"+(i+10));
            iMan.createIngredient(ingredient);

            IngredientAmount item = newItem(null, recipe2.getId(), ingredient, "1 Ks");
            manager.addIngredientInRecipe(item);
            
            items.add(item);
        }
        
        List<IngredientAmount> ingredientsByRecipe = manager.getIngredientsByRecipe(recipe.getId());
        items.removeIf(
                (IngredientAmount i) -> i.getRecipeId()!=recipe.getId()
        );
        items.sort(idComparator);
        ingredientsByRecipe.sort(idComparator);
        assertThat(ingredientsByRecipe.size(),is(equalTo(items.size())));
        assertThat(ingredientsByRecipe,is(equalTo(items)));
    }
    
    //Update tests
    @Test(expected = IllegalArgumentException.class)
    public void updateWithNullIngredientAmount(){
        manager.updateIngredientInRecipe(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateWithNullId(){
        Recipe recipe = newRecipe(null, "Recept", "Příprava", Duration.ofMinutes(25l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibule");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.updateIngredientInRecipe(item);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void updateWithNonExistentId(){
        Recipe recipe = newRecipe(null, "Recept", "Příprava", Duration.ofMinutes(25l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibule");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(20l, recipe.getId(), ingredient, "1 Ks");
        manager.updateIngredientInRecipe(item);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateWithNullRecipeId(){        
        Recipe recipe = newRecipe(null, "Recept", "Příprava", Duration.ofMinutes(25l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibule");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        
        item.setRecipeId(null);
        
        manager.updateIngredientInRecipe(item);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateWithNullIngredient(){        
        Recipe recipe = newRecipe(null, "Recept", "Příprava", Duration.ofMinutes(25l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibule");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        
        item.setIngredient(null);
        
        manager.updateIngredientInRecipe(item);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateWithNullAmount(){        
        Recipe recipe = newRecipe(null, "Recept", "Příprava", Duration.ofMinutes(25l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibule");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        
        item.setAmount(null);
        
        manager.updateIngredientInRecipe(item);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateWithEmptyAmount(){        
        Recipe recipe = newRecipe(null, "Recept", "Příprava", Duration.ofMinutes(25l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibule");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        
        item.setAmount("");
        
        manager.updateIngredientInRecipe(item);
    }
    
    @Test
    public void updateAmountColumnOK(){        
        Recipe recipe = newRecipe(null, "Recept", "Příprava", Duration.ofMinutes(25l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibule");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        
        item.setAmount("2 Ks");
        
        manager.updateIngredientInRecipe(item);
        
        IngredientAmount dbValue = manager.getIngredientAmountById(item.getId());
        
        assertThat(dbValue,is(equalTo(item)));
    }
    
    @Test
    public void updateIngredientColumnOK(){        
        Recipe recipe = newRecipe(null, "Recept", "Příprava", Duration.ofMinutes(25l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibule");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        
        Ingredient ingredient2 = newIngredient(null, "Cibule2");
        iMan.createIngredient(ingredient2);
        item.setIngredient(ingredient2);
        
        manager.updateIngredientInRecipe(item);
        
        IngredientAmount dbValue = manager.getIngredientAmountById(item.getId());
        
        assertThat(dbValue,is(equalTo(item)));
    }
    
    @Test
    public void updateRecipeIdColumnOK(){        
        Recipe recipe = newRecipe(null, "Recept", "Příprava", Duration.ofMinutes(25l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibule");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        
        Recipe recipe2 = newRecipe(null, "Recept2", "Příprava", Duration.ofMinutes(20l));
        rMan.createRecipe(recipe2);
        item.setRecipeId(recipe2.getId());
        
        manager.updateIngredientInRecipe(item);
        
        IngredientAmount dbValue = manager.getIngredientAmountById(item.getId());
        
        assertThat(dbValue,is(equalTo(item)));
    }
    
    //Delete tests
    @Test(expected = IllegalArgumentException.class)
    public void deleteWithNullIngredientAmount(){
        manager.deleteIngredientFromRecipe(null);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void deleteNonExistent(){
        manager.deleteIngredientFromRecipe(20l);
    }
    
    @Test
    public void deleteOK(){
        Recipe recipe = newRecipe(null, "Recept", "Příprava", Duration.ofMinutes(25l));
        rMan.createRecipe(recipe);
        
        Ingredient ingredient = newIngredient(null, "Cibule");
        iMan.createIngredient(ingredient);
        
        IngredientAmount item = newItem(null, recipe.getId(), ingredient, "1 Ks");
        manager.addIngredientInRecipe(item);
        
        manager.deleteIngredientFromRecipe(item.getId());
        
        try{
            IngredientAmount dbValue = manager.getIngredientAmountById(item.getId());
            fail();
        }catch(EntityNotFoundException ex){
            //OK
        }
    }
    
    
    private static final Comparator<IngredientAmount> idComparator = new Comparator<IngredientAmount>() {
        @Override
        public int compare(IngredientAmount o1, IngredientAmount o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
    
    private static IngredientAmount newItem(Long id, Long recipeId, Ingredient ingredient, String amount){
        IngredientAmount item = new IngredientAmount();
        
        item.setId(id);
        item.setRecipeId(recipeId);
        item.setIngredient(ingredient);
        item.setAmount(amount);
        
        return item;
    }
    
    private static Ingredient newIngredient(Long id, String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        
        return ingredient;
    }
    
    private static Recipe newRecipe(Long id, String name, String instructions, Duration duration) {
        Recipe recipe = new Recipe();
        
        recipe.setId(id);
        recipe.setName(name);
        recipe.setInstructions(instructions);
        recipe.setDuration(duration);
        
        return recipe;
    }
}
