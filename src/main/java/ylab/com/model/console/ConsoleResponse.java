package ylab.com.model.console;

import lombok.Value;

@Value
public class ConsoleResponse<T> {
    T obj;
}
