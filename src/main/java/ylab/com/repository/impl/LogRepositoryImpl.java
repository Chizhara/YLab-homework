package ylab.com.repository.impl;

import ylab.com.configure.Connector;
import ylab.com.exception.DatabaseException;
import ylab.com.mapper.LogMapperImpl;
import ylab.com.model.log.Log;
import ylab.com.model.log.LogSearchParams;
import ylab.com.repository.LogRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class LogRepositoryImpl extends CommonSequenceRepository implements LogRepository {

    private final static String sequence = "sys.log_sequence";
    private final LogMapperImpl logMapper;

    public LogRepositoryImpl(Connector connector, LogMapperImpl logMapper) {
        super(connector, sequence);
        this.logMapper = logMapper;
    }

    @Override
    public Log save(Log log) {
        try (Connection connection = openConnection()) {

            Long id = getNextLongSequence(connection);
            log.setId(id);

            String sql = "INSERT INTO develop.logs(id, entity_id, entity_type, event_type, user_id, timestamp, request) ";
            sql += String.format("VALUES (%d, %d, '%s', '%s', %d, '%s', '%s') ",
                log.getId(),
                log.getEntityId(),
                log.getEntityType(),
                log.getEventType(),
                log.getUser().getId(),
                log.getTimestamp(),
                log.getRequest());

            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return log;
    }

    @Override
    public List<Log> findByParams(LogSearchParams params) {
        try (Connection connection = openConnection()) {
            String sql = "SELECT l.id AS log_id, l.entity_id, l.entity_type, l.event_type, l.timestamp, l.request, " +
                "u.id AS user_id, u.login, u.password, u.role, u.email, u.phone " +
                "FROM develop.logs AS l " +
                "JOIN develop.users AS u ON l.user_id = u.id ";
            sql += "WHERE ";

            if (params.getUser() != null) {
                sql += String.format("u.id = %d AND ", params.getUser().getId());
            }
            if (params.getEventType() != null) {
                sql += String.format("l.event_type = '%s' AND ", params.getEventType().name());
            }
            if (params.getEntityId() != null) {
                sql += String.format("l.entity_id = %d AND ", params.getEntityId());
            }
            if (params.getEntityType() != null) {
                sql += String.format("l.entity_type = '%s' AND ", params.getEntityType().name());
            }
            if(params.getDate() != null) {
                sql += String.format("DATE(l.timestamp) = DATE('%s') ", params.getDate().toInstant());
            }

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            List<Log> logs = logMapper.toLogs(rs);
            return logs;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
