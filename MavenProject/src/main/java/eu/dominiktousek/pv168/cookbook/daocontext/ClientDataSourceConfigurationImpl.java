package eu.dominiktousek.pv168.cookbook.daocontext;

import javax.sql.DataSource;
import org.apache.derby.jdbc.ClientDataSource;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Dominik Tousek (422385)
 */
public class ClientDataSourceConfigurationImpl implements DataSourceConfiguration{    
    private DataSource dataSource;
    private String dataSourceName;
    private int port;
    private String serverName;
    private String databaseName;
    private String user;
    private String password;
    private Boolean active;

    public ClientDataSourceConfigurationImpl() {
    }
    
    public ClientDataSourceConfigurationImpl(String dataSourceName, int port, String serverName, String databaseName, String user, String password, Boolean active) {
        this.dataSourceName = dataSourceName;
        this.port = port;
        this.serverName = serverName;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
        this.active = active;
    }
    
    @Override
    public DataSource getDataSource() {
        if(dataSource==null){
            ClientDataSource ds = new ClientDataSource ();
            ds.setDataSourceName(dataSourceName);
            ds.setPortNumber(port);
            ds.setServerName(serverName);
            ds.setDatabaseName(databaseName);
            ds.setUser(user);
            ds.setPassword(password);
            dataSource = ds;
        }
        return dataSource;
    }

    @Override
    public Boolean isActive() {
        return active;
    }

    @Override
    public void parse(Element ds) throws ConfigLoadFailureException {
        if(ds.hasAttribute("active") && ds.getAttribute("active").equals("true")){
            active = true;
        }
        if(!ds.hasAttribute("name")){
            throw new ConfigLoadFailureException("Bad structure of config file! Missing name atribute of dataSource");
        }
        dataSourceName = ds.getAttribute("name");

        NodeList ip = ds.getElementsByTagName("server-ip");
        if(ip.getLength()!=1){
            throw new ConfigLoadFailureException("Bad structure of config file! cannot parse <server-ip> or missing");
        }
        serverName = ip.item(0).getTextContent();

        NodeList prt = ds.getElementsByTagName("server-port");
        if(prt.getLength()!=1){
            throw new ConfigLoadFailureException("Bad structure of config file! cannot parse <server-port> or missing");
        }
        port = Integer.parseInt(prt.item(0).getTextContent());

        NodeList dbs = ds.getElementsByTagName("database");
        if(dbs.getLength()!=1){
            throw new ConfigLoadFailureException("Bad structure of config file! cannot parse <database> or missing");
        }
        Element db  = (Element) dbs.item(0);

        if(!db.hasAttribute("name")){
            throw new ConfigLoadFailureException("Bad structure of config file! missing atribute name of <database>");
        }
        databaseName = db.getAttribute("name");

        NodeList usrs = db.getElementsByTagName("user");
        if(usrs.getLength()!=1){
            throw new ConfigLoadFailureException("Bad structure of config file! cannot parse <user> or missing");
        }
        Element usr  = (Element) usrs.item(0);
        user = usr.getTextContent();

        NodeList pswds = db.getElementsByTagName("password");
        if(dbs.getLength()!=1){
            throw new ConfigLoadFailureException("Bad structure of config file! cannot parse <password> or missing");
        }
        Element pswd  = (Element) pswds.item(0);
        password = pswd.getTextContent();
    }

    @Override
    public String getName() {
        return dataSourceName;
    }
    
}
