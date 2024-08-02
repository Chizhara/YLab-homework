package ylab.com.model.console;

import lombok.Value;
import ylab.com.model.security.Credentials;

import java.util.Map;

@Value
public class ConsoleRequest {
    HandlerKey handlerKey;
    Map<String, String> rawObject;
    Map<String, String> params;
}
