package eu.dominiktousek.pv168.cookbook.daocontext;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Class responsible for managing datasources
 * 
 * @author Dominik Tousek (422385)
 */
public class DBDataSourceFactory {
    private static final String CONFIG_FILE = "db-config.xml";
    
    private static DataSourceConfiguration defaultDataSourceConfig;
    private static final Map<String,DataSourceConfiguration> dataSources = new HashMap<>();
    
    public static DataSource getDataSource(){
        if(defaultDataSourceConfig==null){
            loadConfig();
        }
        
        return defaultDataSourceConfig.getDataSource();
    }
    
    public static DataSource getDataSource(String dataSourceName){
        if(dataSources.isEmpty()){
            loadConfig();
        }
        
        return dataSources.get(dataSourceName).getDataSource();
    }
    
    private static void loadConfig() throws ConfigLoadFailureException{
        dataSources.clear();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream is = new FileInputStream(CONFIG_FILE);
            Document doc = builder.parse(is);
            
            Element root = doc.getDocumentElement();

            NodeList dataSrces = root.getElementsByTagName("datasource");
            if(dataSrces.getLength()==0){
                throw new ConfigLoadFailureException("Bad structure of config file!");
            }
            Boolean activeFound = false;
            for(int i=0;i<dataSrces.getLength();i++){
                Element ds = (Element) dataSrces.item(i);
                if(!ds.hasAttribute("type")){
                    continue;
                }
                String dataSourceType = ds.getAttribute("type");
                DataSourceConfiguration dsc = null;
                switch(dataSourceType){
                    case "derby-client" : {
                        dsc = new ClientDataSourceConfigurationImpl();
                        dsc.parse(ds);
                        break;
                    }
                    case "embedded" : {
                        dsc = new EmbeddedDataSourceConfigurationImpl();
                        dsc.parse(ds);
                    }
                    default: break;
                }
                
                if(dsc!=null){
                    dataSources.put(dsc.getName(), dsc);
                }
                
                if(ds.hasAttribute("active") && ds.getAttribute("active").equals("true")){
                    activeFound = true;
                    defaultDataSourceConfig = dsc;
                }
                
            }
            if(!activeFound){
                throw new ConfigLoadFailureException("None datasource marked as active!");
            }
            
        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException ex) {
            Logger.getLogger(DBDataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new ConfigLoadFailureException("Loading config file failed!", ex);
        }
    }
}
