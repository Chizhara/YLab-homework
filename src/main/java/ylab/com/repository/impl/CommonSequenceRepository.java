package ylab.com.repository.impl;

import ylab.com.configure.Connector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public abstract class CommonSequenceRepository {

    private final Connector connector;
    private final String sequence;

    public CommonSequenceRepository(Connector connector, String sequence) {
        this.connector = connector;
        this.sequence = sequence;
    }

    protected Long getNextLongSequence(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT nextval('"+sequence+"') as value");

        rs.next();
        Long value = rs.getLong("value");
        return value;
    }

    protected Connection openConnection() throws SQLException {
        return connector.openConnection();
    }
}
