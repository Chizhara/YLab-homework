package ylab.com.console;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ylab.com.in.console.impl.UserConsoleController;
import ylab.com.mapper.UserMapperImpl;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.model.user.User;
import ylab.com.model.user.dto.UserCreateRequest;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.dto.UserSearchRequest;
import ylab.com.model.user.dto.UserUpdateRequest;
import ylab.com.service.AuthService;
import ylab.com.service.LogService;
import ylab.com.service.UserService;
import ylab.com.service.UserServiceTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class UserConsoleControllerTest {

    private static UserConsoleController userConsoleController;
    private static AuthService authService;
    private static UserService userService;
    private static UserMapperImpl userMapper;
    private static LogService logService;
    private static String BASE_PATH;

    @BeforeAll
    public static void setUpBeforeClass() {
        authService = Mockito.mock(AuthService.class);
        userService = Mockito.mock(UserService.class);
        logService = Mockito.mock(LogService.class);
        userMapper = new UserMapperImpl();
        userConsoleController = new UserConsoleController(authService, userService, userMapper, logService);
        BASE_PATH = userConsoleController.getBasePath();
    }

    @Test
    public void testCreateUser() {
        User user = UserServiceTest.initUser(UserRole.ADMIN);
        //user.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
            "role", user.getRole().name(),
            "login", user.getLogin(),
            "password", user.getPassword(),
            "email", user.getEmail(),
            "phone", user.getEmail()
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.POST, BASE_PATH))
            .params(Map.of())
            .rawObject(rawBody)
            .build();

        UserCreateRequest createRequest = userMapper.toUserCreateRequest(rawBody);

        Mockito.when(userService.addUser(createRequest)).thenReturn(user);

        ConsoleResponse<?> response = userConsoleController.handleRequest(request);

        Object res = response.getObj();
        assertEquals(res.getClass(), User.class);
        assertEquals(((User) res).getId(), user.getId());
    }

    @Test
    public void testUpdateUser() {
        User user = UserServiceTest.initUser(UserRole.USER);
        //user.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
            "login", user.getLogin(),
            "password", user.getPassword(),
            "email", user.getEmail(),
            "phone", user.getPhone()
        );

        Map<String, String> rawParams = Map.of(
            "login", user.getLogin(),
            "password", user.getPassword()
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.PATCH, BASE_PATH + "/" + user.getId().toString()))
            .params(rawParams)
            .rawObject(rawBody)
            .build();

        UserUpdateRequest updateRequest = userMapper.toUserUpdateRequest(rawBody);

        Mockito.when(authService.getUser(request)).thenReturn(user);
        Mockito.when(userService.updateUser(user, updateRequest, user.getId())).thenReturn(user);

        ConsoleResponse<?> response = userConsoleController.handleRequest(request);

        Object res = response.getObj();
        assertEquals(res.getClass(), User.class);
        assertEquals(((User) res).getId(), user.getId());
    }

    @Test
    public void testGetUser() {
        User user = UserServiceTest.initUser(UserRole.USER);
        User admin = UserServiceTest.initUser(UserRole.ADMIN);
        //user.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
        );

        Map<String, String> rawParams = Map.of(
            "login", admin.getLogin(),
            "password", admin.getPassword()
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.GET, BASE_PATH + "/" + user.getId().toString()))
            .params(rawParams)
            .rawObject(rawBody)
            .build();

        Mockito.when(authService.getUser(request)).thenReturn(admin);
        Mockito.when(userService.getUser(user.getId())).thenReturn(user);

        ConsoleResponse<?> response = userConsoleController.handleRequest(request);

        Object res = response.getObj();
        assertEquals(res.getClass(), User.class);
        assertEquals(((User) res).getId(), user.getId());
    }

    @Test
    public void testGetUsers() {
        User user = UserServiceTest.initUser(UserRole.USER);
        User admin = UserServiceTest.initUser(UserRole.ADMIN);
        //user.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
            "phone", user.getPhone(),
            "email", user.getEmail(),
            "login", user.getLogin(),
            "roles", user.getRole().name()
        );

        Map<String, String> rawParams = Map.of(
            "login", admin.getLogin(),
            "password", admin.getPassword()
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.GET, BASE_PATH))
            .params(rawParams)
            .rawObject(rawBody)
            .build();

        UserSearchRequest searchRequest = userMapper.toUserSearchRequest(rawBody);

        Mockito.when(authService.getUser(request)).thenReturn(admin);
        Mockito.when(userService.findUsers(searchRequest, admin)).thenReturn(List.of(user));

        ConsoleResponse<?> response = userConsoleController.handleRequest(request);

        Object res = response.getObj();
        assertInstanceOf(List.class, res);
        assertEquals(1, ((List<?>) res).size());
    }
}
