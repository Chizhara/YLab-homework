package ylab.com.repository.impl;

import ylab.com.model.car.Car;
import ylab.com.model.car.CarSearchParams;
import ylab.com.repository.CarRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class InMemoryCarRepository extends InMemoryRepository<UUID, Car> implements CarRepository {
    @Override
    public Car save(Car car) {
        UUID id = UUID.randomUUID();
        car.setId(id);
        return super.save(id, car);
    }

    @Override
    public List<Car> findAll() {
        return super.getAll().stream().toList();
    }

    @Override
    public Optional<Car> findById(UUID id) {
        return super.findByKey(id);
    }

    @Override
    public Car deleteById(UUID id) {
        return super.removeById(id);
    }

    @Override
    public List<Car> findCarsByParams(CarSearchParams carSearchParams) {
        Stream<Car> cars = super.getAll().stream();

        if (carSearchParams.getBrand() != null && !carSearchParams.getBrand().isEmpty()) {
            cars = cars.filter(car -> car.getBrand().equals(carSearchParams.getBrand()));
        }
        if (carSearchParams.getModel() != null && !carSearchParams.getModel().isEmpty()) {
            cars = cars.filter(car -> car.getModel().equals(carSearchParams.getModel()));
        }
        if (carSearchParams.getStatus() != null) {
            cars = cars.filter(car -> car.getStatus() == carSearchParams.getStatus());
        }

        if (carSearchParams.getPrice() != null) {
            cars = filterByPrice(carSearchParams.getPrice(), cars);
        }

        return cars.toList();
    }

    private Stream<Car> filterByPrice(CarSearchParams.PriceParam priceParam, Stream<Car> cars) {
        if (priceParam.isLower()) {
            return cars.filter(car -> car.getPrice() < priceParam.getValue());
        } else {
            return cars.filter(car -> car.getPrice() > priceParam.getValue());
        }
    }
}
