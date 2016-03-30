package eu.dominiktousek.pv168.cookbook.daocontext;

import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Dominik Tousek (422385)
 */
public class EmbeddedDataSourceConfigurationImpl implements DataSourceConfiguration{
    private DataSource dataSource;
    private String dataSourceName;
    private String databaseName;
    private Boolean active;
    
    @Override
    public DataSource getDataSource() {
        if(dataSource==null){
            EmbeddedDataSource ds = new EmbeddedDataSource ();
            ds.setDataSourceName(dataSourceName);
            ds.setDatabaseName(databaseName);
            dataSource = ds;
        }
        return dataSource;
    }

    @Override
    public Boolean isActive() {
        return active;
    }

    @Override
    public void parse(Element ds) {
        if(ds.hasAttribute("active") && ds.getAttribute("active").equals("true")){
            active = true;
        }
        if(!ds.hasAttribute("name")){
            throw new ConfigLoadFailureException("Bad structure of config file! Missing name atribute of dataSource");
        }
        dataSourceName = ds.getAttribute("name");

        NodeList dbs = ds.getElementsByTagName("database");
        if(dbs.getLength()!=1){
            throw new ConfigLoadFailureException("Bad structure of config file! cannot parse <database> or missing");
        }
        Element db  = (Element) dbs.item(0);

        if(!db.hasAttribute("name")){
            throw new ConfigLoadFailureException("Bad structure of config file! missing atribute name of <database>");
        }
        databaseName = db.getAttribute("name");
    }

    @Override
    public String getName() {
        return dataSourceName;
    }
    
}
