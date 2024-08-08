package ylab.com.mapper;

import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.log.Log;
import ylab.com.model.log.LogEntityType;
import ylab.com.model.log.LogEventType;
import ylab.com.model.log.LogSearchParams;
import ylab.com.model.log.dto.LogSearchRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LogMapperImpl {

    private final UserMapperImpl userMapper = new UserMapperImpl();

    public LogSearchRequest toLogSearchRequest(Map<String, String> rawBody) {
        return LogSearchRequest.builder()
            .date(Date.from(Instant.parse(rawBody.get("date"))))
            .userId(Long.parseLong(rawBody.get("user")))
            .entityId(Long.parseLong(rawBody.get("entity")))
            .eventType(LogEventType.valueOf(rawBody.get("eventType")))
            .entityType(LogEntityType.valueOf(rawBody.get("entityType")))
            .build();
    }

    public LogSearchParams toLogSearchParams(LogSearchRequest request) {
        return LogSearchParams.builder()
            .date(request.getDate())
            .entityType(request.getEntityType())
            .eventType(request.getEventType())
            .entityId(request.getEntityId())
            .build();
    }

    public Log toLog(ResultSet rs) throws SQLException {
        return Log.builder()
            .id(rs.getLong("log_id"))
            .user(userMapper.toUser(rs))
            .entityId(rs.getLong("entity_id"))
            .eventType(LogEventType.valueOf(rs.getString("event_type")))
            .entityType(LogEntityType.valueOf(rs.getString("entity_type")))
            .timestamp(rs.getTimestamp("timestamp").toInstant())
            .request(rs.getString("request"))
            .build();
    }

    public List<Log> toLogs(ResultSet rs) throws SQLException {
        List<Log> logs = new LinkedList<>();
        while (rs.next()) {
            logs.add(toLog(rs));
        }
        return logs;
    }

    public String toString(ConsoleRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(request.getHandlerKey().getMethod()).append(" ")
            .append(request.getHandlerKey().getPath()).append(" | ")
            .append(mapToString(request.getParams())).append(" | ")
            .append(mapToString(request.getRawObject()));
        return stringBuilder.toString();
    }

    private String mapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> element : map.entrySet()) {
            stringBuilder
                .append(element.getKey())
                .append(" ")
                .append(element.getValue())
                .append("-");
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }
}
