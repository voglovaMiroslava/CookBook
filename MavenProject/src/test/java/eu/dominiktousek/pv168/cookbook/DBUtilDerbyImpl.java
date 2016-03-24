package eu.dominiktousek.pv168.cookbook;

import eu.dominiktousek.pv168.cookbook.daocontext.DBDataSourceFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Miroslava Voglova on 23. 3. 2016.
 */
public class DBUtilDerbyImpl implements DBUtil {
    
    private final DataSource source;
    
    public DBUtilDerbyImpl() {
        this.source = DBDataSourceFactory.getDataSource();
    }

    @Override
    public void prepareDatabase() {
        if(!tableExists("Ingredient")){
            executeScript("CreateIngredientTable.sql");
        }
        if(!tableExists("Recipe")){
            executeScript("CreateRecipeTable.sql");
        }
        if(!tableExists("IngredientAmount")){
            executeScript("CreateIngredientAmountTable.sql");
        }
    }
    
    @Override
    public void clearDatabase(){
        try (Connection con = source.getConnection()) {
            String query=loadStringResource("GetAllTablesFromCurrentSchemaPrioritized.sql");
            if(query==null||query.isEmpty()){
                throw new IOException("Resource file not found!");
            }
            
            PreparedStatement prepareStatement = con.prepareStatement(query);
            ResultSet executeQuery = prepareStatement.executeQuery();
            List<String> tables = new ArrayList<>();
            while(executeQuery.next()){
                tables.add(executeQuery.getString("TABLENAME"));
            }
            for(String table:tables){
                con.prepareStatement("DELETE FROM "+table).executeUpdate();
            }
        }catch(SQLException | IOException ex){
            Logger.getLogger(DBUtilDerbyImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String loadStringResource(String resourceName){
        try(BufferedReader bReader = new BufferedReader(
                new InputStreamReader(
                new FileInputStream("sql/"+resourceName)
            ))){
            String line = bReader.readLine();
            StringBuilder builder = new StringBuilder();
            while(line!=null){
                builder.append(System.lineSeparator());
                builder.append(line);
                line = bReader.readLine();
            }
            return builder.toString().replace(";", "");
        }catch(IOException ex){
            Logger.getLogger(DBUtilDerbyImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void executeScript(String scriptName){
        try (Connection con = source.getConnection()) {
            
            String query=loadStringResource(scriptName);
            if(query==null||query.isEmpty()){
                throw new IOException("Resource file '"+scriptName+"' not found!");
            }
            
            con.prepareStatement(query).execute();
            
        }catch(SQLException | IOException ex){
            Logger.getLogger(DBUtilDerbyImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean tableExists(String tableName) throws ServiceFailureException{
        if(tableName==null || tableName.isEmpty()){
            throw new IllegalArgumentException("Argument tableName can't be null or empty!");
        }
        try (Connection con = source.getConnection()) {
            
            String query=loadStringResource("TableExists.sql");
            if(query==null||query.isEmpty()){
                throw new IOException("Resource file 'TableExists.sql' not found!");
            }
            PreparedStatement prepareStatement = con.prepareStatement(query);
            prepareStatement.setString(1, tableName);
            ResultSet set = prepareStatement.executeQuery();
            Integer result=0;
            if(set.next()){
                result = set.getInt(1);
            }
            else{
                throw new ServiceFailureException("No results retrieved from database!");
            }
            if(set.next()){
                throw new ServiceFailureException("Too many results retrieved from database!");
            }
            
            return result==1;
            
        }catch(SQLException | IOException ex){
            Logger.getLogger(DBUtilDerbyImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServiceFailureException("Error occurred during sql query execution!", ex);
        }
    }
}
