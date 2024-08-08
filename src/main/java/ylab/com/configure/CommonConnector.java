package ylab.com.configure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CommonConnector implements Connector {
    private static final PostgresConnector INSTANCE = new PostgresConnector();
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;

    public CommonConnector(String url, String username, String password) {
        URL = url;
        USERNAME = username;
        PASSWORD = password;
    }

    public static PostgresConnector getInstance() {
        return INSTANCE;
    }

    public Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        connection.setAutoCommit(false);
        return connection;
    }
}
