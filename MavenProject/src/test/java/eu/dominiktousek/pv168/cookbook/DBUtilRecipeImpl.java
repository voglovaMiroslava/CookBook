package eu.dominiktousek.pv168.cookbook;

import eu.dominiktousek.pv168.cookbook.daocontext.DBDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Miroslava Voglova on 23. 3. 2016.
 */
public class DBUtilRecipeImpl implements DBUtil {
    
    private final DataSource source;
    private final String twoA4Pages = "6900";
    
    public DBUtilRecipeImpl() {
        this.source = DBDataSourceFactory.getDataSource();
    }
    
    @Override
    public void createTable(){
        try(Connection con = source.getConnection()) {
            con.prepareStatement("CREATE TABLE RECIPE ("
                    + "ID bigint NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "NAME VARCHAR(255) NOT NULL ,"
                    + "INSTRUCTIONS VARCHAR(" + twoA4Pages + ") NOT NULL,"
                    + "DURATION bigint)").executeUpdate();
            
            con.prepareStatement("CREATE TABLE INGREDIENTAMOUNT ("
                    + "ID BIGINT primary key generated always as identity,"
                    + "RECIPEID BIGINT CONSTRAINT ingredientamount_recipeid_ref REFERENCES RECIPE(ID),"
                    + "INGREDIENTID BIGINT CONSTRAINT ingredientamount_ingredientid_ref REFERENCES INGREDIENT(ID),"
                    + "AMOUNT VARCHAR(255))").executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBUtilRecipeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
    }
    
    @Override
    public void removeTable(){
        try (Connection con = source.getConnection()) {
            con.prepareStatement("DROP TABLE INGREDIENTAMOUNT").executeUpdate();
        }catch(SQLException ex){
            Logger.getLogger(DBUtilRecipeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (Connection con = source.getConnection()) {
            con.prepareStatement("DROP TABLE RECIPE").executeUpdate();
        }catch(SQLException ex){
            Logger.getLogger(DBUtilRecipeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
