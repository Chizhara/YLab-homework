package ylab.com.in.console.impl;

import ylab.com.exception.InvalidActionException;
import ylab.com.in.console.AbsInputConsoleController;
import ylab.com.in.console.PathVariableExtractor;
import ylab.com.mapper.UserMapperImpl;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.model.log.LogEventType;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.UserUpdateRequest;
import ylab.com.service.AuthService;
import ylab.com.service.LogService;
import ylab.com.service.UserService;

import java.util.UUID;

public class EmployerConsoleController extends AbsInputConsoleController {

    private final static String BASE_PATH = "/employer";
    private final UserService userService;
    private final UserMapperImpl userMapper;
    private final AuthService authService;
    private final LogService logService;

    public EmployerConsoleController(AuthService authService,
                                     UserService userService,
                                     UserMapperImpl userMapper,
                                     LogService logService) {
        super(BASE_PATH);
        this.userService = userService;
        this.userMapper = userMapper;
        this.authService = authService;
        this.logService = logService;
    }

    @Override
    public ConsoleResponse<?> handleRequest(ConsoleRequest request) {
        HandlerKey handlerKey = request.getHandlerKey();
        if ((handlerKey.getPath()).contains(BASE_PATH + "/") && handlerKey.getMethod() == Method.PATCH) {
            return updateEmployer(request);
        }
        throw new RuntimeException(); //TODO
    }

    private ConsoleResponse<User> updateEmployer(ConsoleRequest request) {
        UUID id = PathVariableExtractor.extractUUID(request.getHandlerKey().getPath(), BASE_PATH.length() + 1);
        UserUpdateRequest userUpdateRequest = userMapper.toUserUpdateRequest(request.getRawObject());
        User requester = authService.getUser(request);
        if (requester.getRole() != UserRole.ADMIN) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }
        User user = userService.updateEmployer(userUpdateRequest, id);
        logService.save(user, user, LogEventType.UPDATE, request);
        return new ConsoleResponse<>(user);
    }
}
