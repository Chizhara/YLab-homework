package ylab.com.repository;

import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import ylab.com.configure.CommonConnector;
import ylab.com.configure.Connector;
import ylab.com.configure.LiquibaseConfig;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarSearchParams;
import ylab.com.model.car.CarStatus;
import ylab.com.repository.impl.CarRepositoryImpl;

import java.sql.SQLException;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CarRepositoryImplTest {

    private static int carIndex = 0;
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    ).withDatabaseName("test").withUsername("postgres").withPassword("root");
    private static CarRepository carRepository;

    @BeforeAll
    public static void setUpBeforeClass() throws SQLException, LiquibaseException {
        postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
        ).withDatabaseName("test").withUsername("postgres").withPassword("root");
        postgres.start();
        String URL = postgres.getJdbcUrl();
        String USERNAME = postgres.getUsername();
        String PASSWORD = postgres.getPassword();
        postgres.start();
        Connector connector = new CommonConnector(URL, USERNAME, PASSWORD);
        carRepository = new CarRepositoryImpl(connector, new CarMapperImpl());
        LiquibaseConfig.configure(connector);
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    public static Car initCar(CarStatus carStatus) {
        ++carIndex;
        return Car.builder()
            .brand("brand_" + carIndex)
            .model("model_" + carIndex)
            .price(1000 + carIndex)
            .releaseYear(Year.of(2020))
            .status(carStatus)
            .statusDescription("Status description for " + carIndex)
            .build();
    }

    @Test
    @DisplayName("Сохранение корректной информации о машине")
    public void test_shouldSave_whenCorrect() {
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
    @DisplayName("Поиск существующей машины по идентификатору")
    public void test_shouldFindById_whenExists() {
        Car car = initCar(CarStatus.NEW);
        car = carRepository.save(car);

        Optional<Car> carResOpt = carRepository.findById(car.getId());

        assertTrue(carResOpt.isPresent());

        Car carRes = carResOpt.get();

        assertEquals(car.getId(), carRes.getId());
    }

    @Test
    @DisplayName("Удаление мащины при существовании")
    public void test_shouldDelete_whenExists() {
        Car car = initCar(CarStatus.NEW);
        car = carRepository.save(car);

        carRepository.deleteById(car.getId());
        Optional<Car> carRes = carRepository.findById(car.getId());

        assertTrue(carRes.isEmpty());
    }

    @Test
    @DisplayName("Поиск существующей машины по всем параметрам")
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
