package ylab.com.model.console;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
public class HandlerKey {
    Method method;
    String path;
}
