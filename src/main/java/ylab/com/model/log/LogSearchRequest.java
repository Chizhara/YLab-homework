package ylab.com.model.log;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class LogSearchRequest {
    private UUID userId;
    private LogEventType eventType;
    private LogEntityType entityType;
    private UUID entityId;
    private Date date;
}
