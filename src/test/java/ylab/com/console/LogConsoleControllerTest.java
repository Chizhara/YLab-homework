package ylab.com.console;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ylab.com.in.console.impl.LogConsoleController;
import ylab.com.mapper.LogMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarStatus;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.model.log.Log;
import ylab.com.model.log.LogEntityType;
import ylab.com.model.log.LogEventType;
import ylab.com.model.log.LogSearchRequest;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.service.AuthService;
import ylab.com.service.CarServiceTest;
import ylab.com.service.LogService;
import ylab.com.service.LogServiceTest;
import ylab.com.service.UserServiceTest;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class LogConsoleControllerTest {
    private static String BASE_PATH;
    private static LogConsoleController controller;
    private static LogService logService;
    private static AuthService authService;
    private static LogMapperImpl logMapper;

    @BeforeAll
    public static void setUpBeforeClass() {
        authService = Mockito.mock(AuthService.class);
        logService = Mockito.mock(LogService.class);
        logMapper = new LogMapperImpl();
        controller = new LogConsoleController(logService, logMapper, authService);
        BASE_PATH = controller.getBasePath();
    }

    @Test
    public void testGetLogs() {
        User admin = UserServiceTest.initUser(UserRole.ADMIN);
        admin.setId(UUID.randomUUID());
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());
        Log log = LogServiceTest.initLog(admin, LogEventType.UPDATE, car, LogEntityType.CAR);
        log.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
            "date", Date.from(log.getTimestamp()).toInstant().toString(),
            "user", admin.getId().toString(),
            "entity", log.getEntityId().toString(),
            "eventType", log.getEventType().name(),
            "entityType", log.getEntityType().name()
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

        LogSearchRequest searchRequest = logMapper.toLogSearchRequest(rawBody);

        Mockito.when(authService.getUser(request)).thenReturn(admin);
        Mockito.when(logService.searchLogs(searchRequest)).thenReturn(List.of(log));

        ConsoleResponse<?> response = controller.handleRequest(request);

        Object res = response.getObj();
        assertInstanceOf(List.class, res);
        assertEquals(1, ((List<?>) res).size());
    }
}
