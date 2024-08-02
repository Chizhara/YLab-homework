package ylab.com.in.console;

import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;

public interface InputConsoleController {
    String getBasePath();

    ConsoleResponse<?> handleRequest(ConsoleRequest request);
}
