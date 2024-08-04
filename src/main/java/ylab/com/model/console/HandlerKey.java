package ylab.com.model.console;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HandlerKey {
    Method method;
    String path;
}
