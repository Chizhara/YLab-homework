package ylab.com.console;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ylab.com.in.console.impl.CarConsoleController;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarCreateRequest;
import ylab.com.model.car.CarSearchRequest;
import ylab.com.model.car.CarStatus;
import ylab.com.model.car.CarUpdateRequest;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.service.AuthService;
import ylab.com.service.CarService;
import ylab.com.service.CarServiceTest;
import ylab.com.service.LogService;
import ylab.com.service.UserServiceTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class CarConsoleControllerTest {
    private static CarConsoleController carConsoleController;
    private static AuthService authService;
    private static CarMapperImpl carMapper;
    private static CarService carService;
    private static String BASE_PATH;

    @BeforeAll
    public static void setUpBeforeClass() {
        carMapper = new CarMapperImpl();
        carService = Mockito.mock(CarService.class);
        authService = Mockito.mock(AuthService.class);
        LogService logService = Mockito.mock(LogService.class);
        carConsoleController = new CarConsoleController(carService, carMapper, authService, logService);
        BASE_PATH = carConsoleController.getBasePath();
    }

    @Test
    public void testAddCar() {
        User user = UserServiceTest.initUser(UserRole.MANAGER);
        user.setId(UUID.randomUUID());
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
            "brand", car.getBrand(),
            "model", car.getModel(),
            "price", car.getPrice().toString(),
            "releaseYear", car.getReleaseYear().toString(),
            "status", car.getStatus().toString()
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

        CarCreateRequest carCreateRequest = carMapper.toCarCreateRequest(rawBody);


        Mockito.when(authService.getUser(request)).thenReturn(user);
        Mockito.when(carService.addCar(carCreateRequest)).thenReturn(car);

        ConsoleResponse<?> response = carConsoleController.handleRequest(request);

        assertInstanceOf(Car.class, response.getObj());
        assertEquals(car.getId(), ((Car) response.getObj()).getId());
    }

    @Test
    public void testUpdateCar() {
        User user = UserServiceTest.initUser(UserRole.MANAGER);
        user.setId(UUID.randomUUID());
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
            "price", car.getPrice().toString(),
            "status", car.getStatus().toString()
        );

        Map<String, String> rawParams = Map.of(
            "login", user.getLogin(),
            "password", user.getPassword()
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.PATCH, BASE_PATH + "/" + car.getId()))
            .params(rawParams)
            .rawObject(rawBody)
            .build();

        CarUpdateRequest updateRequest = carMapper.toCarUpdateRequest(rawBody);

        Mockito.when(authService.getUser(request)).thenReturn(user);
        Mockito.when(carService.updateCar(car.getId(), updateRequest)).thenReturn(car);

        ConsoleResponse<?> response = carConsoleController.handleRequest(request);

        assertInstanceOf(Car.class, response.getObj());
        assertEquals(car.getId(), ((Car) response.getObj()).getId());
    }

    @Test
    public void testDelete() {
        User user = UserServiceTest.initUser(UserRole.MANAGER);
        user.setId(UUID.randomUUID());
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
        );

        Map<String, String> rawParams = Map.of(
            "login", user.getLogin(),
            "password", user.getPassword()
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.DELETE, BASE_PATH + "/" + car.getId()))
            .params(rawParams)
            .rawObject(rawBody)
            .build();

        Mockito.when(authService.getUser(request)).thenReturn(user);
        Mockito.when(carService.removeCar(car.getId())).thenReturn(car);

        ConsoleResponse<?> response = carConsoleController.handleRequest(request);

        assertInstanceOf(Car.class, response.getObj());
        assertEquals(car.getId(), ((Car) response.getObj()).getId());
    }

    @Test
    public void testGetCar() {
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
        );

        Map<String, String> rawParams = Map.of(
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.GET, BASE_PATH + "/" + car.getId()))
            .params(rawParams)
            .rawObject(rawBody)
            .build();

        Mockito.when(carService.getCar(car.getId())).thenReturn(car);

        ConsoleResponse<?> response = carConsoleController.handleRequest(request);

        assertInstanceOf(Car.class, response.getObj());
        assertEquals(car.getId(), ((Car) response.getObj()).getId());
    }

    @Test
    public void testSearchCars() {
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());

        Map<String, String> rawBody = Map.of(
            "brand", car.getBrand(),
            "model", car.getModel(),
            "price value", String.valueOf(car.getPrice() + 1),
            "price lower", "true",
            "releaseYear", car.getReleaseYear().toString(),
            "status", car.getStatus().toString()
        );

        Map<String, String> rawParams = Map.of(
        );

        ConsoleRequest request = ConsoleRequest.builder()
            .handlerKey(new HandlerKey(Method.GET, BASE_PATH))
            .params(rawParams)
            .rawObject(rawBody)
            .build();

        CarSearchRequest searchRequest = carMapper.toCarSearchRequest(rawBody);

        Mockito.when(carService.findCar(searchRequest)).thenReturn(List.of(car));

        ConsoleResponse<?> response = carConsoleController.handleRequest(request);

        Object res = response.getObj();
        assertInstanceOf(List.class, res);
        assertEquals(1, ((List<?>) res).size());
    }

}
