package ylab.com.model.log;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.user.User;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Log {
    private Long id;
    private User user;
    private LogEventType eventType;
    private LogEntityType entityType;
    private Long entityId;
    private String request;
    private Instant timestamp;

}
