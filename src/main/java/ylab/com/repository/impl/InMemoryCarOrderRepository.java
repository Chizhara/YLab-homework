package ylab.com.repository.impl;

import ylab.com.model.Car;
import ylab.com.model.CarOrder;
import ylab.com.model.CarOrderStatus;
import ylab.com.model.User;
import ylab.com.repository.CarOrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public List<CarOrder> findByCustomer(User customer) {
        UUID customerId = customer.getId();
        return storage.values().stream()
            .filter(order ->
                order.getCustomer().getId().equals(customerId))
            .toList();
    }

    @Override
    public List<CarOrder> findByCar(Car car) {
        UUID carId = car.getId();
        return storage.values().stream()
            .filter(order ->
                order.getCar().getId().equals(carId))
            .toList();
    }

    @Override
    public List<CarOrder> findByCustomerAndStatus(User user, CarOrderStatus orderStatus) {
        UUID customerId = user.getId();
        return storage.values().stream()
            .filter(order ->
                order.getCustomer().getId().equals(customerId) &&
                    order.getCarOrderStatus().equals(orderStatus))
            .toList();
    }

    @Override
    public List<CarOrder> findByCarAndStatus(Car car, CarOrderStatus orderStatus) {
        UUID carId = car.getId();
        return storage.values().stream()
            .filter(order ->
                order.getCar().getId().equals(carId) &&
                    order.getCarOrderStatus().equals(orderStatus))
            .toList();
    }
}
