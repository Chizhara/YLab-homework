package ylab.com.model.console;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ConsoleRequest {
    HandlerKey handlerKey;
    Map<String, String> rawObject;
    Map<String, String> params;
}
