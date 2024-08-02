package ylab.com.repository;

import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderSearchParams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarOrderRepository {
    CarOrder save(CarOrder car);
    Optional<CarOrder> findById(UUID id);
    List<CarOrder> findOrdersByParams(CarOrderSearchParams params);
}
