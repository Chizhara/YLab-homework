package ylab.com.configure;

import java.sql.Connection;
import java.sql.SQLException;

public interface Connector {
    Connection openConnection() throws SQLException;
}
