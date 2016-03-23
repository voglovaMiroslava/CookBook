package eu.dominiktousek.pv168.cookbook;

import java.sql.SQLException;

/**
 * Created by Miroslava Voglova
 */
public interface DBUtil {

    void createTable() throws SQLException;

    void removeTable() throws SQLException;
}
