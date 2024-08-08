package ylab.com.model.log.dto;

import lombok.Builder;
import lombok.Data;
import ylab.com.model.log.LogEntityType;
import ylab.com.model.log.LogEventType;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class LogSearchRequest {
    private Long userId;
    private LogEventType eventType;
    private LogEntityType entityType;
    private Long entityId;
    private Date date;
}
