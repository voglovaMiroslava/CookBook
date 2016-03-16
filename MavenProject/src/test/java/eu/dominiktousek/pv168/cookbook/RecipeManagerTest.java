/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dominiktousek.pv168.cookbook;

import org.junit.*;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Dominik
 */

public class RecipeManagerTest {

    private RecipeManager manager;

    @Before
    public void init(){
        manager = new RecipeManagerImpl();
        clearData();
    }

    @Test
    public void createRecipeAllGood(){
        Recipe rec = createRecipe("Bramboracka","Boil water, add potatoes", Duration.ofHours(5L));
        manager.createRecipe(rec);

        assertThat(rec.getId(), is(not(equalTo(null))));
        assertThat(rec.getName(), is(equalTo("Bramboracka")));
        assertThat(rec.getInstructions(), is(equalTo("Boil water, add potatoes")));
        assertThat(rec.getDuration(), is(equalTo(Duration.ofHours(5L))));
    }

    @Test
    public void createRecipeWithoutDuration(){
        Recipe rec = createRecipe("Bramboracka","Boil water, add potatoes", null);
        manager.createRecipe(rec);

        assertThat(rec.getId(), is(not(equalTo(null))));
        assertThat(rec.getName(), is(equalTo("Bramboracka")));
        assertThat(rec.getInstructions(), is(equalTo("Boil water, add potatoes")));
        assertThat(rec.getDuration(), is(equalTo(null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRecipeNullName(){
        Recipe rec = createRecipe(null, "instructions", null);
        manager.createRecipe(rec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRecipeEmptyName(){
        Recipe rec = createRecipe("","instructions",null);
        manager.createRecipe(rec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRecipeNullInstruction(){
        Recipe rec = createRecipe("name", null, null);
        manager.createRecipe(rec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRecipeEmptyInstruction(){
        Recipe rec = createRecipe("name","",null);
        manager.createRecipe(rec);
    }

    @Test
    public void updateRecipeGood(){
        Recipe rec = createRecipe("name","instructions",  Duration.ofHours(2L));
        manager.createRecipe(rec);
        rec.setDuration(Duration.ofMinutes(54L));
        manager.updateRecipe(rec);
        Recipe other = manager.getRecipeById(rec.getId());
        assertTrue("One of the attributes does not match. Update failed.",checkAttributes(rec, other));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRecipeNullName(){

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRecipeEmptyName(){

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRecipeNullInstruction(){

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRecipeEmptyInstruction(){

    }
/*
    @Test(expected = UnsupportedOperationException.class)
    public void updateRecipeNewId(){
        //nesmi dovolit upravu id, pokud id uz nastaveno
    }
*/
    @Test
    public void returnsAllRecipes(){

    }

    @Test
    public void searchRecipeById(){

    }

    @Test
    public void durationSearchBothDurationsSet(){
        //ve vysledku nesmi byt recipe s null duration
    }

    @Test
    public void durationSearchOneDurationsSet(){
        //absence horni hranice == od
        //absence dolni hranice == do
        //ve vysledku nesmi byt recipe s null duration
    }

    @Test
    public void durationSearchNoneDurationsSet(){
        //melo by vratit vsechno
    }

    @Test
    public void searchByName(){

    }

    @Test
    public void searchByEmptyName(){
        //melo by vratit vsechno, search vyhledava full textove

    }

    @Test
    public void searchByNullName(){
        // vyhodit null argument exception
    }

    private static void clearData(){

    }

    private static boolean checkAttributes(Recipe one, Recipe other){
        if (one == null && other == null) {
            return true;
        }

        if(one == null || other == null) {
            return false;
        }

        boolean equality = true;

        equality = equality && checkNullAndEquality(one.getId(), other.getId());
        equality = equality && checkNullAndEquality(one.getDuration(), other.getDuration());
        equality = equality && checkNullAndEquality(one.getInstructions(), other.getInstructions());
        equality = equality && checkNullAndEquality(one.getName(), other.getName());

        return equality;
    }

    private static boolean checkNullAndEquality(Object one, Object other){
        if (one != null){
            return one.equals(other);
        }

        return other == null;
    }

    private static Recipe createRecipe(String name, String instruction, Duration duration){
        Recipe rec = new Recipe();

        rec.setName(name);
        rec.setInstructions(instruction);
        rec.setDuration(duration);

        return rec;
    }
}
