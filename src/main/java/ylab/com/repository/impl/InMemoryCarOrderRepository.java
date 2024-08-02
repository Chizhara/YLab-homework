package ylab.com.repository.impl;

import ylab.com.model.car.Car;
import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderSearchParams;
import ylab.com.model.order.CarOrderStatus;
import ylab.com.model.user.User;
import ylab.com.repository.CarOrderRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class InMemoryCarOrderRepository extends InMemoryRepository<UUID, CarOrder> implements CarOrderRepository {
    @Override
    public CarOrder save(CarOrder order) {
        UUID id = UUID.randomUUID();
        order.setId(id);
        return super.save(id, order);
    }

    @Override
    public Optional<CarOrder> findById(UUID id) {
        return super.findBy(id);
    }

    @Override
    public List<CarOrder> findOrdersByParams(CarOrderSearchParams params) {
        Stream<CarOrder> orders = super.getAll().stream();
        if (params.getCustomer() != null) {
            orders = filterByCustomer(params.getCustomer(), orders);
        }
        if (params.getCar() != null) {
            orders = filterByCar(params.getCar(), orders);
        }
        if (params.getStatus() != null) {
            orders = filterByStatus(params.getStatus(), orders);
        }
        if (params.getOrderDate() != null) {
            orders = filterByOrderDate(params.getOrderDate(), orders);
        }
        return orders.toList();
    }

    public Stream<CarOrder> filterByCustomer(User customer, Stream<CarOrder> orders) {
        UUID customerId = customer.getId();
        return orders
            .filter(order ->
                order.getCustomer().getId().equals(customerId));
    }

    public Stream<CarOrder> filterByCar(Car car, Stream<CarOrder> orders) {
        UUID carId = car.getId();
        return orders
            .filter(order ->
                order.getCar().getId().equals(carId));
    }

    public Stream<CarOrder> filterByStatus(CarOrderStatus orderStatus, Stream<CarOrder> orders) {
        return orders
            .filter(order ->
                order.getCarOrderStatus().equals(orderStatus));
    }

    public Stream<CarOrder> filterByOrderDate(Instant orderDate, Stream<CarOrder> orders) {
        return orders
            .filter(order ->
                order.getDate().equals(orderDate));
    }
}
