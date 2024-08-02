package ylab.com.repository;

import ylab.com.model.car.Car;
import ylab.com.model.car.CarSearchParams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarRepository {
    Car save(Car car);
    List<Car> findAll();
    Optional<Car> findById(UUID id);
    Car deleteById(UUID id);
    List<Car> findCarsByParams(CarSearchParams carSearchParams);
}
