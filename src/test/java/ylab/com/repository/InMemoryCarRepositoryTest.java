package ylab.com.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarSearchParams;
import ylab.com.model.car.CarStatus;
import ylab.com.repository.impl.InMemoryCarRepository;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryCarRepositoryTest {
    private static int carIndex = 0;
    private static CarRepository carRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        carRepository = new InMemoryCarRepository();
    }

    public static Car initCar(CarStatus carStatus) {
        carIndex++;
        return Car.builder()
            .brand("brand_" + carIndex)
            .model("model_" + carIndex)
            .price(1000 + carIndex)
            .releaseYear(Year.of(2020))
            .status(carStatus)
            .build();
    }

    @Test
    public void testSave() {
        Car car = initCar(CarStatus.NEW);
        Car carRes = carRepository.save(car);

        assertNotNull(carRes.getId());
        assertEquals(car.getModel(), carRes.getModel());
        assertEquals(car.getBrand(), carRes.getBrand());
        assertEquals(car.getPrice(), carRes.getPrice());
        assertEquals(car.getReleaseYear(), carRes.getReleaseYear());
        assertEquals(car.getStatus(), carRes.getStatus());
    }

    @Test
    public void testFindById() {
        Car car = initCar(CarStatus.NEW);
        car = carRepository.save(car);

        Optional<Car> carResOpt = carRepository.findById(car.getId());

        assertTrue(carResOpt.isPresent());

        Car carRes = carResOpt.get();

        assertEquals(car.getId(), carRes.getId());
    }

    @Test
    public void testDelete() {
        Car car = initCar(CarStatus.NEW);
        car = carRepository.save(car);

        Car carRes = carRepository.deleteById(car.getId());

        assertEquals(car.getId(), carRes.getId());

        Optional<Car> carCheck = carRepository.findById(car.getId());

        assertTrue(carCheck.isEmpty());
    }

    @Test
    public void testFindByParams() {
        Car car = initCar(CarStatus.NEW);
        car = carRepository.save(car);

        CarSearchParams params = CarSearchParams.builder()
            .status(CarStatus.NEW)
            .brand(car.getBrand())
            .model(car.getModel())
            .price(CarSearchParams.PriceParam.builder()
                .value(car.getPrice() - 1)
                .lower(false)
                .build())
            .releaseYear(car.getReleaseYear())
            .build();

        List<Car> carsRes = carRepository.findCarsByParams(params);

        assertEquals(1, carsRes.size());

        Car carRes = carsRes.get(0);

        assertEquals(car.getId(), carRes.getId());
    }
}
