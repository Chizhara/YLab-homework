package ylab.com.model.log;

import lombok.Builder;
import lombok.Data;
import ylab.com.model.user.User;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class LogSearchParams {
    private User user;
    private LogEventType eventType;
    private LogEntityType entityType;
    private UUID entityId;
    private Date date;
}
