package ylab.com.repository;

import ylab.com.model.Car;
import ylab.com.model.CarOrder;
import ylab.com.model.CarOrderStatus;
import ylab.com.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarOrderRepository {
    CarOrder save(CarOrder car);
    Optional<CarOrder> findById(UUID id);
    List<CarOrder> findByCustomer(User user);
    List<CarOrder> findByCar(Car car);
    List<CarOrder> findByCustomerAndStatus(User user, CarOrderStatus orderStatus);
    List<CarOrder> findByCarAndStatus(Car car, CarOrderStatus orderStatus);
}
