package eu.dominiktousek.pv168.cookbook.daocontext;

import javax.sql.DataSource;
import org.w3c.dom.Element;

/**
 *
 * @author Dominik Tousek (422385)
 */
public interface DataSourceConfiguration {

    DataSource getDataSource();
    
    Boolean isActive();
            
    void parse(Element ds);
    
    String getName();
    
}
