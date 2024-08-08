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
import ylab.com.model.user.dto.UserCreateRequest;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.dto.UserSearchRequest;
import ylab.com.model.user.dto.UserUpdateRequest;
import ylab.com.service.AuthService;
import ylab.com.service.LogService;
import ylab.com.service.UserService;

import java.util.List;
import java.util.UUID;

public class UserConsoleController extends AbsInputConsoleController {

    private final static String BASE_PATH = "/user";
    private final UserService userService;
    private final UserMapperImpl userMapper;
    private final AuthService authService;
    private final LogService logService;

    public UserConsoleController(AuthService authService,
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
        if ((BASE_PATH).equals(handlerKey.getPath()) && handlerKey.getMethod() == Method.POST) {
            return addUser(request);
        } else if ((handlerKey.getPath()).contains(BASE_PATH + "/") && handlerKey.getMethod() == Method.PATCH) {
            return updateUser(request);
        } else if ((BASE_PATH).equals(handlerKey.getPath()) && handlerKey.getMethod() == Method.GET) {
            return getUsers(request);
        } else if ((handlerKey.getPath()).contains(BASE_PATH + "/") && handlerKey.getMethod() == Method.GET) {
            return getUser(request);
        }
        throw new RuntimeException(); //TODO
    }

    private ConsoleResponse<User> addUser(ConsoleRequest request) {
        UserCreateRequest userCreateRequest = userMapper.toUserCreateRequest(request.getRawObject());
        return new ConsoleResponse<>(userService.addUser(userCreateRequest));
    }

    private ConsoleResponse<User> updateUser(ConsoleRequest request) {
        Long id = PathVariableExtractor.extractLong(request.getHandlerKey().getPath(), BASE_PATH.length() + 1);
        UserUpdateRequest userUpdateRequest = userMapper.toUserUpdateRequest(request.getRawObject());
        User requester = authService.getUser(request);
        User user = userService.updateUser(requester, userUpdateRequest, id);
        logService.save(user, user, LogEventType.UPDATE, request);
        return new ConsoleResponse<>(user);
    }

    private ConsoleResponse<User> getUser(ConsoleRequest request) {
        Long id = PathVariableExtractor.extractLong(request.getHandlerKey().getPath(), BASE_PATH.length() + 1);
        User requester = authService.getUser(request);
        if (requester.getRole() != UserRole.ADMIN) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }
        User user = userService.getUser(id);
        return new ConsoleResponse<>(user);
    }

    private ConsoleResponse<List<User>> getUsers(ConsoleRequest request) {
        User requester = authService.getUser(request);
        UserSearchRequest searchRequest = userMapper.toUserSearchRequest(request.getRawObject());

        if (requester.getRole() == UserRole.USER) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }

        List<User> users = userService.findUsers(searchRequest, requester);
        return new ConsoleResponse<>(users);
    }
}
