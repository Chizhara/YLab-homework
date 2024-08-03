package ylab.com.in.console.impl;

import ylab.com.exception.InvalidActionException;
import ylab.com.in.console.AbsInputConsoleController;
import ylab.com.in.console.PathVariableExtractor;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.ConsoleResponse;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderCreateRequest;
import ylab.com.model.order.CarOrderSearchRequest;
import ylab.com.model.order.CarOrderUpdateRequest;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.service.AuthService;
import ylab.com.service.CarOrderService;

import java.util.List;
import java.util.UUID;

public class CarOrderConsoleController extends AbsInputConsoleController {
    private final static String BASE_PATH = "/car/order";

    private final CarMapperImpl carMapper;
    private final CarOrderService carOrderService;
    private final AuthService authService;

    public CarOrderConsoleController(CarMapperImpl carMapper, CarOrderService carOrderService, AuthService authService) {
        super(BASE_PATH);
        this.carMapper = carMapper;
        this.carOrderService = carOrderService;
        this.authService = authService;
    }

    @Override
    public ConsoleResponse<?> handleRequest(ConsoleRequest request) {
        HandlerKey handlerKey = request.getHandlerKey();
        if (BASE_PATH.equals(handlerKey.getPath()) && handlerKey.getMethod().equals(Method.POST)) {
            return addOrder(request);
        } else if (BASE_PATH.equals(handlerKey.getPath()) && handlerKey.getMethod().equals(Method.GET)) {
            return getOrders(request);
        } else if (handlerKey.getPath().contains((BASE_PATH + "/")) && handlerKey.getMethod().equals(Method.PATCH)) {
            return updateOrder(request);
        }
        throw new RuntimeException(); //TODO
    }

    private ConsoleResponse<CarOrder> addOrder(ConsoleRequest request) {
        CarOrderCreateRequest createRequest = carMapper.toCarOrderCreateRequest(request.getRawObject());
        User user = authService.getUser(request);
        if (user.getRole() != UserRole.USER) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }
        CarOrder carOrder = carOrderService.addOrder(user, createRequest);
        return new ConsoleResponse<>(carOrder);
    }

    private ConsoleResponse<CarOrder> updateOrder(ConsoleRequest request) {
        UUID id = PathVariableExtractor.extractUUID(request.getHandlerKey().getPath(), BASE_PATH.length() + 1);
        CarOrderUpdateRequest updateRequest = carMapper.toCarOrderUpdateRequest(request.getRawObject());
        User user = authService.getUser(request);
        if (user.getRole() != UserRole.MANAGER && user.getRole() != UserRole.ADMIN) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }
        CarOrder carOrder = carOrderService.updateOrder(id, updateRequest);
        return new ConsoleResponse<>(carOrder);
    }

    private ConsoleResponse<List<CarOrder>> getOrders(ConsoleRequest request) {
        CarOrderSearchRequest searchRequest = carMapper.toCarOrderSearchRequest(request.getRawObject());

        User user = authService.getUser(request);
        if (user.getRole() != UserRole.MANAGER && user.getRole() != UserRole.ADMIN) {
            throw new InvalidActionException("У вас недостаточно прав для совершения действия");
        }

        List<CarOrder> carOrders = carOrderService.findOrders(searchRequest);
        return new ConsoleResponse<>(carOrders);
    }
}
