package ylab.com.in.console.impl;

import ylab.com.exception.InvalidActionException;
import ylab.com.in.console.AbsInputConsoleController;
import ylab.com.mapper.LogMapperImpl;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.model.log.Log;
import ylab.com.model.log.LogSearchRequest;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.service.AuthService;
import ylab.com.service.LogService;

import java.util.List;

public class LogConsoleController extends AbsInputConsoleController {

    private final static String BASE_PATH = "/log";
    private final LogService logService;
    private final LogMapperImpl logMapper;
    private final AuthService authService;

    public LogConsoleController(LogService logService,
                                LogMapperImpl logMapper,
                                AuthService authService) {
        super(BASE_PATH);
        this.logService = logService;
        this.logMapper = logMapper;
        this.authService = authService;
    }

    @Override
    public ConsoleResponse<?> handleRequest(ConsoleRequest request) {
        HandlerKey handlerKey = request.getHandlerKey();
        if (BASE_PATH.equals(handlerKey.getPath()) && handlerKey.getMethod().equals(Method.GET)) {
            return getLogs(request);
        } else if ((BASE_PATH + "/export").equals(handlerKey.getPath()) && handlerKey.getMethod().equals(Method.GET)) {
            return exportLogs(request);
        }

        throw new RuntimeException();
    }

    private ConsoleResponse<List<Log>> getLogs(ConsoleRequest request) {
        LogSearchRequest searchRequest = logMapper.toLogSearchRequest(request.getRawObject());

        User requester = authService.getUser(request);
        if (requester.getRole() != UserRole.ADMIN) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }
        List<Log> logs = logService.searchLogs(searchRequest);
        return new ConsoleResponse<>(logs);
    }

    private ConsoleResponse<?> exportLogs(ConsoleRequest request) {
        LogSearchRequest searchRequest = logMapper.toLogSearchRequest(request.getRawObject());

        User requester = authService.getUser(request);
        if (requester.getRole() != UserRole.ADMIN) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }
        logService.exportLogs(searchRequest);
        return new ConsoleResponse<>(null);
    }
}
