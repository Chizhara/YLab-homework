package ylab.com.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ylab.com.exception.InvalidActionException;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarStatus;
import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderCreateRequest;
import ylab.com.model.order.CarOrderSearchParams;
import ylab.com.model.order.CarOrderSearchRequest;
import ylab.com.model.order.CarOrderStatus;
import ylab.com.model.order.CarOrderUpdateRequest;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.repository.CarOrderRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CarOrderServiceTest {
    private static int orderIndex = 0;
    private static CarOrderService carOrderService;
    private static CarOrderRepository orderRepository;
    private static CarMapperImpl carMapper;
    private static UserService userService;
    private static CarService carService;

    @BeforeAll
    public static void setUpBeforeClass() {
        carService = Mockito.mock(CarService.class);
        userService = Mockito.mock(UserService.class);
        orderRepository = Mockito.mock(CarOrderRepository.class);
        carMapper = new CarMapperImpl();
        carOrderService = new CarOrderService(orderRepository, carMapper, userService, carService);
    }

    public static CarOrder initCarOrder(Car car, User customer) {
        orderIndex++;
        return CarOrder.builder()
            .id((long) orderIndex)
            .customer(customer)
            .car(car)
            .carOrderStatus(CarOrderStatus.CREATED)
            .date(Instant.now())
            .build();
    }

    @Test
    public void testAddCarOrder() {
        User customer = UserServiceTest.initUser(UserRole.USER);
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        CarOrder carOrder = initCarOrder(car, customer);

        CarOrderCreateRequest request = CarOrderCreateRequest.builder()
            .carId(car.getId())
            .build();

        Mockito.when(carService.getCar(car.getId())).thenReturn(car);
        Mockito.when(orderRepository
            .save(ArgumentMatchers.argThat(temp ->
                temp.getCar().equals(car) && temp.getCustomer().equals(customer)
                    && temp.getCarOrderStatus().equals(CarOrderStatus.CREATED))))
            .thenReturn(carOrder);

        CarOrder orderRes = carOrderService.addOrder(customer, request);

        assertEquals(CarOrderStatus.CREATED, orderRes.getCarOrderStatus());
        assertEquals(car, orderRes.getCar());
        assertEquals(customer, orderRes.getCustomer());
    }

    @Test
    public void testSearchCarOrder() {
        User customer = UserServiceTest.initUser(UserRole.USER);
        //customer.setId(UUID.randomUUID());
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        //car.setId(UUID.randomUUID());
        CarOrder carOrder = initCarOrder(car, customer);

        CarOrderSearchRequest request = CarOrderSearchRequest.builder()
            .carId(car.getId())
            .customerId(customer.getId())
            .status(List.of(carOrder.getCarOrderStatus()))
            .build();

        CarOrderSearchParams params = carMapper.carToCarOrderSearchParams(request, customer, car);

        Mockito.when(carService.getCar(car.getId())).thenReturn(car);
        Mockito.when(userService.getUser(customer.getId())).thenReturn(customer);
        Mockito.when(orderRepository.findOrdersByParams(params)).thenReturn(List.of(carOrder));

        List<CarOrder> ordersRes = carOrderService.findOrders(request);

        assertEquals(1, ordersRes.size());

        CarOrder orderRes = ordersRes.get(0);

        assertEquals(carOrder.getId(), orderRes.getId());
        assertEquals(carOrder.getCarOrderStatus(), orderRes.getCarOrderStatus());
        assertEquals(carOrder.getCar(), orderRes.getCar());
        assertEquals(carOrder.getCustomer(), orderRes.getCustomer());
        assertEquals(carOrder.getDate(), orderRes.getDate());
    }

    @Test
    public void testCloseCarOrder() {
        User customer = UserServiceTest.initUser(UserRole.MANAGER);
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        CarOrder carOrder = initCarOrder(car, customer);

        CarOrder carOrderClone = CarOrder.builder()
            .id(carOrder.getId())
            .customer(carOrder.getCustomer())
            .car(carOrder.getCar())
            .carOrderStatus(carOrder.getCarOrderStatus())
            .date(carOrder.getDate())
            .build();

        carOrderClone.setCarOrderStatus(CarOrderStatus.CLOSED);

        CarOrderUpdateRequest request = CarOrderUpdateRequest.builder()
            .carOrderStatus(CarOrderStatus.CLOSED)
            .build();

        Mockito.when(orderRepository.findById(carOrder.getId())).thenReturn(Optional.of(carOrder));
        Mockito.when(orderRepository.save(carOrderClone)).thenReturn(carOrderClone);

        CarOrder orderRes = carOrderService.updateOrder(carOrder.getId(), request);

        assertEquals(carOrderClone.getId(), orderRes.getId());
        assertEquals(carOrderClone.getCarOrderStatus(), orderRes.getCarOrderStatus());
        assertEquals(carOrderClone.getCar(), orderRes.getCar());
        assertEquals(carOrderClone.getCustomer(), orderRes.getCustomer());
        assertEquals(carOrderClone.getDate(), orderRes.getDate());
    }

    @Test
    public void testCancelCarOrder() {
        User customer = UserServiceTest.initUser(UserRole.MANAGER);
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        CarOrder carOrder = initCarOrder(car, customer);
        UUID id = UUID.randomUUID();
        //carOrder.setId(id);

        CarOrder carOrderClone = CarOrder.builder()
            .id(carOrder.getId())
            .customer(carOrder.getCustomer())
            .car(carOrder.getCar())
            .carOrderStatus(carOrder.getCarOrderStatus())
            .date(carOrder.getDate())
            .build();

        carOrderClone.setCarOrderStatus(CarOrderStatus.CANCELED);

        CarOrderUpdateRequest request = CarOrderUpdateRequest.builder()
            .carOrderStatus(CarOrderStatus.CANCELED)
            .build();

        Mockito.when(orderRepository.findById(carOrder.getId())).thenReturn(Optional.of(carOrder));
        Mockito.when(orderRepository.save(carOrderClone)).thenReturn(carOrderClone);

        CarOrder orderRes = carOrderService.updateOrder(carOrder.getId(), request);

        assertEquals(carOrderClone.getId(), orderRes.getId());
        assertEquals(carOrderClone.getCarOrderStatus(), orderRes.getCarOrderStatus());
        assertEquals(carOrderClone.getCar(), orderRes.getCar());
        assertEquals(carOrderClone.getCustomer(), orderRes.getCustomer());
        assertEquals(carOrderClone.getDate(), orderRes.getDate());
    }

    @Test
    public void testCloseClosedCarOrder() {
        User customer = UserServiceTest.initUser(UserRole.MANAGER);
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        CarOrder carOrder = initCarOrder(car, customer);
        carOrder.setCarOrderStatus(CarOrderStatus.CLOSED);

        CarOrderUpdateRequest request = CarOrderUpdateRequest.builder()
            .carOrderStatus(CarOrderStatus.CLOSED)
            .build();

        Mockito.when(orderRepository.findById(carOrder.getId())).thenReturn(Optional.of(carOrder));

        assertThrows(InvalidActionException.class, () -> carOrderService.updateOrder(carOrder.getId(), request));
    }

    @Test
    public void testCancelClosedCarOrder() {
        User customer = UserServiceTest.initUser(UserRole.MANAGER);
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        CarOrder carOrder = initCarOrder(car, customer);
        carOrder.setCarOrderStatus(CarOrderStatus.CANCELED);

        CarOrderUpdateRequest request = CarOrderUpdateRequest.builder()
            .carOrderStatus(CarOrderStatus.CLOSED)
            .build();

        Mockito.when(orderRepository.findById(carOrder.getId())).thenReturn(Optional.of(carOrder));

        assertThrows(InvalidActionException.class, () -> carOrderService.updateOrder(carOrder.getId(), request));
    }

    @Test
    public void testCloseCanceledCarOrder() {
        User customer = UserServiceTest.initUser(UserRole.MANAGER);
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        CarOrder carOrder = initCarOrder(car, customer);
        carOrder.setCarOrderStatus(CarOrderStatus.CLOSED);

        CarOrderUpdateRequest request = CarOrderUpdateRequest.builder()
            .carOrderStatus(CarOrderStatus.CANCELED)
            .build();

        Mockito.when(orderRepository.findById(carOrder.getId())).thenReturn(Optional.of(carOrder));

        assertThrows(InvalidActionException.class, () -> carOrderService.updateOrder(carOrder.getId(), request));
    }
}
