package ylab.com.in.console.impl;

import ylab.com.exception.InvalidActionException;
import ylab.com.in.console.AbsInputConsoleController;
import ylab.com.in.console.PathVariableExtractor;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarCreateRequest;
import ylab.com.model.car.CarSearchRequest;
import ylab.com.model.car.CarUpdateRequest;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.model.log.LogEventType;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.service.AuthService;
import ylab.com.service.CarService;
import ylab.com.service.LogService;

import java.util.List;
import java.util.UUID;

public class CarConsoleController extends AbsInputConsoleController {

    private final static String BASE_PATH = "/car";
    private final CarService carService;
    private final CarMapperImpl carMapper;
    private final AuthService authService;
    private final LogService logService;

    public CarConsoleController(CarService carService,
                                CarMapperImpl carMapper,
                                AuthService authService, LogService logService) {
        super(BASE_PATH);
        this.carService = carService;
        this.carMapper = carMapper;
        this.authService = authService;
        this.logService = logService;
    }

    @Override
    public ConsoleResponse<?> handleRequest(ConsoleRequest request) {
        HandlerKey handlerKey = request.getHandlerKey();
        if (BASE_PATH.equals(handlerKey.getPath()) && handlerKey.getMethod().equals(Method.POST)) {
            return addCar(request);
        } else if (BASE_PATH.equals(handlerKey.getPath()) && handlerKey.getMethod().equals(Method.GET)) {
            return getCars(request);
        } else if (handlerKey.getPath().contains((BASE_PATH + "/")) && handlerKey.getMethod().equals(Method.GET)) {
            return getCar(request);
        } else if (handlerKey.getPath().contains((BASE_PATH + "/")) && handlerKey.getMethod().equals(Method.PATCH)) {
            return updateCar(request);
        } else if (handlerKey.getPath().contains((BASE_PATH + "/")) && handlerKey.getMethod().equals(Method.DELETE)) {
            return deleteCar(request);
        }
        throw new RuntimeException(); //TODO
    }

    private ConsoleResponse<Car> addCar(ConsoleRequest request) {
        CarCreateRequest createRequest = carMapper.toCarCreateRequest(request.getRawObject());
        User user = authService.getUser(request);
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.MANAGER) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }

        Car car = carService.addCar(createRequest);
        logService.save(user, car, LogEventType.POST, request);
        return new ConsoleResponse<>(car);
    }

    private ConsoleResponse<Car> updateCar(ConsoleRequest request) {
        CarUpdateRequest updateRequest = carMapper.toCarUpdateRequest(request.getRawObject());
        Long id = PathVariableExtractor.extractLong(request.getHandlerKey().getPath(), BASE_PATH.length() + 1);
        User user = authService.getUser(request);
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.MANAGER) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }

        Car car = carService.updateCar(id, updateRequest);
        logService.save(user, car, LogEventType.UPDATE, request);
        return new ConsoleResponse<>(car);
    }

    private ConsoleResponse<Car> getCar(ConsoleRequest request) {
        Long id = PathVariableExtractor.extractLong(request.getHandlerKey().getPath(), BASE_PATH.length() + 1);
        Car car = carService.getCar(id);
        return new ConsoleResponse<>(car);
    }

    private ConsoleResponse<List<Car>> getCars(ConsoleRequest request) {
        CarSearchRequest searchRequest = carMapper.toCarSearchRequest(request.getRawObject());
        List<Car> cars = carService.findCar(searchRequest);
        return new ConsoleResponse<>(cars);
    }

    private ConsoleResponse<Car> deleteCar(ConsoleRequest request) {
        Long id = PathVariableExtractor.extractLong(request.getHandlerKey().getPath(), BASE_PATH.length() + 1);
        User user = authService.getUser(request);
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.MANAGER) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }

        Car car = carService.removeCar(id);
        logService.save(user, car, LogEventType.DELETE, request);
        return new ConsoleResponse<>(car);
    }
}
