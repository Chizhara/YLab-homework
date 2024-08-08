package ylab.com.configure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnector implements Connector {

    private static final PostgresConnector INSTANCE = new PostgresConnector();
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;

    public PostgresConnector() {
        URL = PropertiesStorage.getInstance().getProperty("datasource.url");
        USERNAME = PropertiesStorage.getInstance().getProperty("datasource.username");
        PASSWORD = PropertiesStorage.getInstance().getProperty("datasource.password");
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
