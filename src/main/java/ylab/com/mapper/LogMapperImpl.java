package ylab.com.mapper;

import ylab.com.model.log.LogEntityType;
import ylab.com.model.log.LogEventType;
import ylab.com.model.log.LogSearchParams;
import ylab.com.model.log.LogSearchRequest;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class LogMapperImpl {

    public LogSearchRequest toLogSearchRequest(Map<String, String> rawBody) {
        return LogSearchRequest.builder()
            .date(Date.from(Instant.parse(rawBody.get("date"))))
            .userId(UUID.fromString(rawBody.get("user")))
            .entityId(UUID.fromString(rawBody.get("entity")))
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
}
