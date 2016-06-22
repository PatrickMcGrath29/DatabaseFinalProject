package cs3200.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by patrickmcgrath on 6/21/16.
 */
public class FrontEndModel implements IModel {

    private final String serverName = "cs3200db.crhtlnps61ag.us-east-1.rds.amazonaws.com";
    private final int portNumber = 3307;
    private final String dbName = "UniversityTracker";
    private String dbuserName = "PatrickMcGrath29";
    private String dbpassword = "zilbyrox";

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.dbuserName);
        connectionProps.put("password", this.dbpassword);
        conn = DriverManager.getConnection("jdbc:mysql://" + this.serverName
                + ":" + this.portNumber + "/"
                + this.dbName, connectionProps);
        return conn;
    }
}
