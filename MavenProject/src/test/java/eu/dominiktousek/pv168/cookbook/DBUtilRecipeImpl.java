package eu.dominiktousek.pv168.cookbook;

import eu.dominiktousek.pv168.cookbook.daocontext.DBDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Miroslava Voglova on 23. 3. 2016.
 */
public class DBUtilRecipeImpl implements DBUtil {

    private DataSource source;
    private final String twoA4Pages = "6900";

    public DBUtilRecipeImpl(){
        this.source = DBDataSourceFactory.getDataSource();
    }

    @Override
    public void createTable() throws SQLException {
        try(Connection con = source.getConnection()){
            con.prepareStatement("CREATE TABLE RECIPE ("
            + "ID bigint NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
            + "NAME VARCHAR(255) NOT NULL ,"
            + "INSTRUCTIONS VARCHAR(" + twoA4Pages +") NOT NULL,"
            + "DURATION bigint)").executeUpdate();
        }

    }

    @Override
    public void removeTable() throws SQLException {
        try(Connection con = source.getConnection()){
            con.prepareStatement("DROP TABLE RECIPE").executeUpdate();
        }
    }
}
