package cs3200.Model;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by patrickmcgrath on 6/21/16.
 */
public interface IModel {

    Connection getConnection() throws SQLException;

}
