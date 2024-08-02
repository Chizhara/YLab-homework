package ylab.com.service;

import lombok.RequiredArgsConstructor;
import ylab.com.exception.NotFoundException;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderCreateRequest;
import ylab.com.model.order.CarOrderSearchParams;
import ylab.com.model.order.CarOrderSearchRequest;
import ylab.com.model.order.CarOrderStatus;
import ylab.com.model.order.CarOrderUpdateRequest;
import ylab.com.model.user.User;
import ylab.com.repository.CarOrderRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CarOrderService {
    private final CarOrderRepository carOrderRepository;
    private final CarMapperImpl carMapper;
    private final UserService userService;
    private final CarService carService;

    public CarOrder addOrder(CarOrderCreateRequest request) {
        User customer = userService.getUser(request.getCustomerId());
        Car car = carService.getCar(request.getCarId());
        CarOrder order = createOrder(customer, car);
        return carOrderRepository.save(order);
    }

    public List<CarOrder> findOrders(CarOrderSearchRequest request) {
        User customer = null;
        Car car = null;
        if(request.getCarId() != null) {
            car = carService.getCar(request.getCarId());
        }
        if(request.getCustomerId() != null) {
            customer = userService.getUser(request.getCustomerId());
        }
        CarOrderSearchParams orderSearchParams = carMapper.carToCarOrderSearchParams(request, customer, car);
        return carOrderRepository.findOrdersByParams(orderSearchParams);
    }

    public CarOrder getOrder(UUID id) {
        return carOrderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(CarOrder.class, id));
    }

    public CarOrder updateOrder(UUID orderId, CarOrderUpdateRequest request) {
        CarOrder order = getOrder(orderId);
        CarOrderStatus status = order.getCarOrderStatus();
        CarOrderStatus newStatus = request.getCarOrderStatus();
        if (status == newStatus) {
            throw new IllegalArgumentException("Статус должен отличаться от существующего");
        }
        if (status == CarOrderStatus.CANCELED) {
            throw new IllegalArgumentException("Нельзя изменить статус отмененной заявки");
        }
        if (status == CarOrderStatus.CLOSED) {
            throw new IllegalArgumentException("Нельзя изменить статус закрытой заявки");
        }

        order.setCarOrderStatus(newStatus);
        return carOrderRepository.save(order);
    }

    private CarOrder createOrder(User customer, Car car) {
        return CarOrder.builder()
            .car(car)
            .customer(customer)
            .date(Instant.now())
            .carOrderStatus(CarOrderStatus.CREATED)
            .build();
    }
}
