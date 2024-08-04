package ylab.com.model.console;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
@Data
@ToString
@AllArgsConstructor
public class ConsoleResponse<T> {
    private final T obj;
}
