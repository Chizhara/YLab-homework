package ylab.com.console;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ylab.com.exception.InvalidActionException;
import ylab.com.in.console.impl.EmployerConsoleController;
import ylab.com.mapper.UserMapperImpl;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.dto.UserUpdateRequest;
import ylab.com.service.AuthService;
import ylab.com.service.LogService;
import ylab.com.service.UserService;
import ylab.com.service.UserServiceTest;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmployerConsoleControllerTest {
    private static EmployerConsoleController employerConsoleController;
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
        employerConsoleController = new EmployerConsoleController(authService, userService, userMapper, logService);
        BASE_PATH = employerConsoleController.getBasePath();
    }

    @Test
    public void testUpdateEmployer() {
        User user = UserServiceTest.initUser(UserRole.MANAGER);
        User admin = UserServiceTest.initUser(UserRole.ADMIN);
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

        Mockito.when(authService.getUser(request)).thenReturn(admin);
        Mockito.when(userService.updateEmployer(updateRequest, user.getId())).thenReturn(user);

        ConsoleResponse<?> response = employerConsoleController.handleRequest(request);

        Object res = response.getObj();
        assertEquals(res.getClass(), User.class);
        assertEquals(((User) res).getId(), user.getId());
    }

    @Test
    public void testUpdateEmployerByUser() {
        User user = UserServiceTest.initUser(UserRole.MANAGER);
        User admin = UserServiceTest.initUser(UserRole.USER);
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

        Mockito.when(authService.getUser(request)).thenReturn(admin);

        assertThrows(InvalidActionException.class, () -> employerConsoleController.handleRequest(request));
    }
}
