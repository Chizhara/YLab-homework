package ylab.com.in.console;

import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InputConsoleDispatcher {
    private final Map<String, InputConsoleController> controllers;

    public InputConsoleDispatcher(Collection<InputConsoleController> controllers) {
        this.controllers = new HashMap<>();
        initProcessors(controllers);
    }

    private void initProcessors(Collection<InputConsoleController> controllers) {
        controllers.forEach(controller -> this.controllers.put(controller.getBasePath(), controller));
    }

    public ConsoleResponse<?> dispatch(ConsoleRequest request) {
        String path = request.getHandlerKey().getPath();
        while (true) {
            if (controllers.containsKey(path)) {
                return controllers.get(path).handleRequest(request);
            }

            path = trimPath(path);
        }
    }

    private String trimPath(String path) {
        int index = path.lastIndexOf("/");
        if (index == -1) {
            throw new RuntimeException(); //TODO
        }
        return path.substring(0, index);
    }
}
