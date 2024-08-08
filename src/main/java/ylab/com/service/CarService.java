package ylab.com.service;

import lombok.RequiredArgsConstructor;
import ylab.com.exception.InvalidActionException;
import ylab.com.exception.NotFoundException;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarCreateRequest;
import ylab.com.model.car.CarSearchParams;
import ylab.com.model.car.CarSearchRequest;
import ylab.com.model.car.CarUpdateRequest;
import ylab.com.model.order.CarOrderStatus;
import ylab.com.repository.CarOrderRepository;
import ylab.com.repository.CarRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CarService {
    private final CarOrderRepository carOrderRepository;
    private final CarRepository carRepository;
    private final CarMapperImpl carMapper;

    public Car getCar(Long id) {
        return carRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(Car.class, id));
    }

    public List<Car> getCars() {
        return carRepository.findAll();
    }

    public List<Car> findCar(CarSearchRequest request) {
        CarSearchParams params = carMapper.toCarSearchParams(request);
        List<Car> cars = carRepository.findCarsByParams(params);
        cars = cars.stream()
            .filter(car ->
                !carOrderRepository
                    .containsByCarAndStatuses(car.getId(), List.of(CarOrderStatus.CREATED, CarOrderStatus.CLOSED)))
            .toList();
        return cars;
    }

    public Car addCar(CarCreateRequest request) {
        Car car = carMapper.toCar(request);
        return carRepository.save(car);
    }

    public Car updateCar(Long id, CarUpdateRequest request) {
        Car car = getCar(id);
        validateCarIsAvailable(car);
        car = carMapper.toCar(car, request);
        return carRepository.save(car);
    }

    public Car removeCar(Long id) {
        Car car = getCar(id);
        validateCarIsAvailable(car);
        carRepository.deleteById(id);
        return car;
    }

    private void validateCarIsAvailable(Car car) {
        boolean contains = carOrderRepository.containsByCarAndStatuses(car.getId(),
            List.of(CarOrderStatus.CREATED, CarOrderStatus.CLOSED));

        if (contains) {
            throw new InvalidActionException("Автомобиль уже продан либо зарезервирован");
        }
    }

}
