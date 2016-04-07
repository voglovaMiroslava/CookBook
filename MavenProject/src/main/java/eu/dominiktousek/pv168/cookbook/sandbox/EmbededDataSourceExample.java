package eu.dominiktousek.pv168.cookbook.sandbox;

import eu.dominiktousek.pv168.cookbook.Ingredient;
import eu.dominiktousek.pv168.cookbook.IngredientManager;
import eu.dominiktousek.pv168.cookbook.IngredientManagerImpl;
import eu.dominiktousek.pv168.cookbook.daocontext.*;
import java.util.List;
import javax.sql.DataSource;

/**
 *
 * @author Dominik
 */
public class EmbededDataSourceExample {
    public static void main(String[] args){
        DataSource ds = DBDataSourceFactory.getDataSource("cookbook-test inmemory not persistent cache");
        DBUtil dbKeeper = new DBUtilDerbyImpl(ds); 
        dbKeeper.prepareDatabase();
        
        IngredientManager man = new IngredientManagerImpl(ds);
        List<Ingredient> items = man.searchByName("Cibule");
        System.out.println(items.size() + " items found in DB before store command");
        Ingredient item = new Ingredient();
        item.setName("Cibule");
        man.createIngredient(item);
        
        items = man.searchByName("Cibule");
        System.out.println(items.size() + " items found in DB after store command");
    }
}
