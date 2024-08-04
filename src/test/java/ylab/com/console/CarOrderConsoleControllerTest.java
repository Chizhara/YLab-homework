package ylab.com.console;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ylab.com.in.console.impl.CarOrderConsoleController;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarStatus;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderCreateRequest;
import ylab.com.model.order.CarOrderSearchRequest;
import ylab.com.model.order.CarOrderStatus;
import ylab.com.model.order.CarOrderUpdateRequest;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.service.AuthService;
import ylab.com.service.CarOrderService;
import ylab.com.service.CarOrderServiceTest;
import ylab.com.service.CarServiceTest;
import ylab.com.service.LogService;
import ylab.com.service.UserServiceTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class CarOrderConsoleControllerTest {

    private static CarOrderConsoleController orderController;
    private static AuthService authService;
    private static CarMapperImpl carMapper;
    private static CarOrderService orderService;
    private static String BASE_PATH;
    private static LogService logService;

    @BeforeAll
    public static void setUpBeforeClass() {
        carMapper = new CarMapperImpl();
        orderService = Mockito.mock(CarOrderService.class);
        authService = Mockito.mock(AuthService.class);
        logService = Mockito.mock(LogService.class);
        orderController = new CarOrderConsoleController(carMapper, orderService, authService, logService);
        BASE_PATH = orderController.getBasePath();
    }

    @Test
    public void testAddOrder() {
        User user = UserServiceTest.initUser(UserRole.USER);
        user.setId(UUID.randomUUID());
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());
        CarOrder order = CarOrderServiceTest.initCarOrder(car, user);
        order.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
            "car", car.getId().toString()
        );

        Map<String, String> rawParams = Map.of(
            "login", user.getLogin(),
            "password", user.getPassword()
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.POST, BASE_PATH))
            .params(rawParams)
            .rawObject(rawBody)
            .build();

        CarOrderCreateRequest createRequest = carMapper.toCarOrderCreateRequest(rawBody);

        Mockito.when(authService.getUser(request)).thenReturn(user);
        Mockito.when(orderService.addOrder(user, createRequest)).thenReturn(order);

        ConsoleResponse<?> response = orderController.handleRequest(request);

        assertInstanceOf(CarOrder.class, response.getObj());
        assertEquals(order.getId(), ((CarOrder) response.getObj()).getId());
    }

    @Test
    public void testSearchOrders() {
        User user = UserServiceTest.initUser(UserRole.MANAGER);
        user.setId(UUID.randomUUID());
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());
        CarOrder order = CarOrderServiceTest.initCarOrder(car, user);
        order.setId(UUID.randomUUID());
        order.setCarOrderStatus(CarOrderStatus.CLOSED);

        Map<String, String> rawBody = Map.of(
            "car", order.getCar().getId().toString(),
            "customer", order.getCustomer().getId().toString(),
            "status", order.getCarOrderStatus().toString(),
            "orderDate", order.getDate().toString()
        );

        Map<String, String> rawParams = Map.of(
            "login", user.getLogin(),
            "password", user.getPassword()
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.GET, BASE_PATH))
            .params(rawParams)
            .rawObject(rawBody)
            .build();

        CarOrderSearchRequest searchRequest = carMapper.toCarOrderSearchRequest(rawBody);

        Mockito.when(authService.getUser(request)).thenReturn(user);
        Mockito.when(orderService.findOrders(searchRequest)).thenReturn(List.of(order));

        ConsoleResponse<?> response = orderController.handleRequest(request);

        Object res = response.getObj();

        assertInstanceOf(List.class, res);
        assertEquals(1, ((List<?>) res).size());
    }

    @Test
    public void testUpdateOrder() {
        User user = UserServiceTest.initUser(UserRole.MANAGER);
        user.setId(UUID.randomUUID());
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());
        CarOrder order = CarOrderServiceTest.initCarOrder(car, user);
        order.setId(UUID.randomUUID());
        order.setCarOrderStatus(CarOrderStatus.CLOSED);

        Map<String, String> rawBody = Map.of(
            "status", order.getCarOrderStatus().name()
        );

        Map<String, String> rawParams = Map.of(
            "login", user.getLogin(),
            "password", user.getPassword()
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.PATCH, BASE_PATH + "/" + order.getId()))
            .params(rawParams)
            .rawObject(rawBody)
            .build();

        CarOrderUpdateRequest updateRequest = carMapper.toCarOrderUpdateRequest(rawBody);

        Mockito.when(authService.getUser(request)).thenReturn(user);
        Mockito.when(orderService.updateOrder(order.getId(), updateRequest)).thenReturn(order);

        ConsoleResponse<?> response = orderController.handleRequest(request);

        assertInstanceOf(CarOrder.class, response.getObj());
        assertEquals(order.getId(), ((CarOrder) response.getObj()).getId());
    }

}
