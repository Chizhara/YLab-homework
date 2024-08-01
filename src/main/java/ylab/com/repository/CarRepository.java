package ylab.com.repository;

import ylab.com.model.Car;

import java.util.Optional;
import java.util.UUID;

public interface CarRepository {
    Car save(Car car);
    Optional<Car> findById(UUID id);
    Car deleteById(UUID id);
}
