package ylab.com.service;

import lombok.RequiredArgsConstructor;
import ylab.com.exception.NotFoundException;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarCreateRequest;
import ylab.com.model.car.CarSearchParams;
import ylab.com.model.car.CarSearchRequest;
import ylab.com.model.car.CarUpdateRequest;
import ylab.com.repository.CarRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CarService {
    public final CarRepository carRepository;
    private final CarMapperImpl carMapper;

    public Car getCar(UUID id) {
        return carRepository.findById(id)
            .orElseThrow( () -> new NotFoundException(Car.class, id));
    }

    public List<Car> getCars() {
        return carRepository.findAll();
    }

    public List<Car> findCar(CarSearchRequest request) {
        CarSearchParams params = carMapper.toCarSearchParams(request);
        return carRepository.findCarsByParams(params);
    }

    public Car addCar(CarCreateRequest request) {
        Car car = carMapper.toCar(request);
        return carRepository.save(car);
    }

    public Car updateCar(UUID id, CarUpdateRequest request) {
        Car car = getCar(id);
        car = carMapper.toCar(car, request);
        return carRepository.save(car);
    }

    public Car removeCar(UUID id) {
        getCar(id);
        return carRepository.deleteById(id);
    }

}
