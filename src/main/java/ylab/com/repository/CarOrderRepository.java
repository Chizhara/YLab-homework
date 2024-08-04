package ylab.com.repository;

import ylab.com.model.car.Car;
import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderSearchParams;
import ylab.com.model.order.CarOrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarOrderRepository {
    CarOrder save(CarOrder car);

    Optional<CarOrder> findById(UUID id);

    boolean containsByCarAndStatuses(Car car, List<CarOrderStatus> orderStatuses);

    List<CarOrder> findOrdersByParams(CarOrderSearchParams params);
}
