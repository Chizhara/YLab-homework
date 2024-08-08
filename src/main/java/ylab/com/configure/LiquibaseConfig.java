package ylab.com.configure;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;

public class LiquibaseConfig {

    public static void configure() throws LiquibaseException, SQLException {
        Connection connection = PostgresConnector.getInstance().openConnection();
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        PropertiesStorage propertiesStorage = PropertiesStorage.getInstance();
        String mainFile = propertiesStorage.getProperty("liquibase.change-log");
        Liquibase liquibase = new liquibase.Liquibase(mainFile, new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts());
    }

    public static void configure(Connector connector) throws LiquibaseException, SQLException {
        Connection connection = connector.openConnection();
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        PropertiesStorage propertiesStorage = PropertiesStorage.getInstance();
        String mainFile = propertiesStorage.getProperty("liquibase.change-log");
        Liquibase liquibase = new liquibase.Liquibase(mainFile, new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts());
    }

}
