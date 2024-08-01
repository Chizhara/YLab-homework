package ylab.com.repository.impl;

import ylab.com.model.Car;
import ylab.com.repository.CarRepository;

import java.util.Optional;
import java.util.UUID;

public class InMemoryCarRepository extends InMemoryRepository<UUID, Car> implements CarRepository {
    @Override
    public Car save(Car car) {
        UUID id = UUID.randomUUID();
        car.setId(id);
        return super.save(id, car);
    }

    @Override
    public Optional<Car> findById(UUID id) {
        return super.findByKey(id);
    }

    @Override
    public Car deleteById(UUID id) {
        return super.removeById(id);
    }
}
