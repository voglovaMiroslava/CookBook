package eu.dominiktousek.pv168.cookbook.daocontext;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.derby.jdbc.ClientDataSource;

/**
 * Class responsible for managing datasources
 * 
 * @author Dominik Tousek (422385)
 */
public class DBDataSourceFactory {
    private static final String CONFIG_FILE = "db-config.xml";
    
    private static DataSource dataSource;
    private static String dataSourceName;
    private static int port;
    private static String serverName;
    private static String databaseName;
    private static String user;
    private static String password;
    
    public static DataSource getDataSource(){
        if(dataSource==null){
            loadConfig();
            
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
    
    private static void loadConfig() throws ConfigLoadFailureException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream is = new FileInputStream(CONFIG_FILE);
            Document doc = builder.parse(is);
            
            Element root = doc.getDocumentElement();

            NodeList dataSources = root.getElementsByTagName("datasource");
            if(dataSources.getLength()==0){
                throw new ConfigLoadFailureException("Bad structure of config file!");
            }
            Element ds = null;
            for(int i=0;i<dataSources.getLength();i++){
                Element el = (Element) dataSources.item(i);
                if(el.hasAttribute("active") && el.getAttribute("active").equals("true")){
                    ds = el;
                }
            }
            if(ds==null){
                throw new ConfigLoadFailureException("None datasource marked as active!");
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
            
        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException ex) {
            Logger.getLogger(DBDataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new ConfigLoadFailureException("Loading config file failed!", ex);
        }
    }
}
