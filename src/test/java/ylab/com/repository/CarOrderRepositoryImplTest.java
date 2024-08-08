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
import ylab.com.mapper.UserMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarStatus;
import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderSearchParams;
import ylab.com.model.order.CarOrderStatus;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.repository.impl.CarOrderRepositoryImpl;
import ylab.com.repository.impl.CarRepositoryImpl;
import ylab.com.repository.impl.UserRepositoryImpl;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CarOrderRepositoryImplTest {

    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    ).withDatabaseName("test").withUsername("postgres").withPassword("root");

    private static CarOrderRepository carOrderRepository;
    private static UserRepository userRepository;
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
        carOrderRepository = new CarOrderRepositoryImpl(connector, new CarMapperImpl());
        userRepository = new UserRepositoryImpl(connector, new UserMapperImpl());
        LiquibaseConfig.configure(connector);
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    public static CarOrder initCarOrder(Car car, User customer) {
        return CarOrder.builder()
            .customer(customer)
            .car(car)
            .carOrderStatus(CarOrderStatus.CREATED)
            .date(Instant.now())
            .build();
    }

    @Test
    @DisplayName("Сохранение корректной информации о заказе")
    public void test_shouldSave_whenCorrect() {
        User customer = UserRepositoryImplTest.initUser(UserRole.USER);
        Car car = CarRepositoryImplTest.initCar(CarStatus.NEW);
        carRepository.save(car);
        userRepository.save(customer);
        CarOrder order = initCarOrder(car, customer);

        CarOrder orderRes = carOrderRepository.save(order);

        assertNotNull(orderRes.getId());
        assertEquals(order.getCar(), orderRes.getCar());
        assertEquals(order.getCarOrderStatus(), orderRes.getCarOrderStatus());
        assertEquals(order.getDate(), orderRes.getDate());
        assertEquals(order.getCustomer(), orderRes.getCustomer());
    }

    @Test
    @DisplayName("Поиск существующего заказа по идентификатору")
    public void test_shouldFindById_whenExists() {
        User customer = UserRepositoryImplTest.initUser(UserRole.USER);
        Car car = CarRepositoryImplTest.initCar(CarStatus.NEW);
        carRepository.save(car);
        userRepository.save(customer);
        CarOrder order = initCarOrder(car, customer);
        order = carOrderRepository.save(order);

        Optional<CarOrder> orderResOpt = carOrderRepository.findById(order.getId());

        assertTrue(orderResOpt.isPresent());
        assertEquals(order.getId(), orderResOpt.get().getId());
    }

    @Test
    @DisplayName("Проверка существования существующего заказа по машине и статусу")
    public void testContainsByCarAndStatuses() {
        User customer = UserRepositoryImplTest.initUser(UserRole.USER);
        Car car = CarRepositoryImplTest.initCar(CarStatus.NEW);
        carRepository.save(car);
        userRepository.save(customer);
        CarOrder order = initCarOrder(car, customer);
        order = carOrderRepository.save(order);

        boolean res = carOrderRepository.containsByCarAndStatuses(car.getId(), List.of(order.getCarOrderStatus()));

        assertTrue(res);
    }

    @Test
    @DisplayName("Поиск существующего заказа по всем параметрам")
    public void test_shouldFindByAllParams_whenExists() {
        User customer = UserRepositoryImplTest.initUser(UserRole.USER);
        Car car = CarRepositoryImplTest.initCar(CarStatus.NEW);
        carRepository.save(car);
        userRepository.save(customer);
        CarOrder order = initCarOrder(car, customer);
        order = carOrderRepository.save(order);

        CarOrderSearchParams params = CarOrderSearchParams.builder()
            .car(car)
            .customer(customer)
            .status(List.of(order.getCarOrderStatus()))
            .orderDate(order.getDate())
            .build();

        List<CarOrder> carOrders = carOrderRepository.findOrdersByParams(params);

        assertEquals(1, carOrders.size());
        assertEquals(order.getId(), carOrders.get(0).getId());
        assertEquals(car.getId(), carOrders.get(0).getCar().getId());
        assertEquals(customer.getId(), carOrders.get(0).getCustomer().getId());
    }

}
