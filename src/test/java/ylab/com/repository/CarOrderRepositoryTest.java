package ylab.com.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarStatus;
import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderSearchParams;
import ylab.com.model.order.CarOrderStatus;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.repository.impl.InMemoryCarOrderRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CarOrderRepositoryTest {
    private static CarOrderRepository carOrderRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        carOrderRepository = new InMemoryCarOrderRepository();
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
    public void testSave() {
        User customer = InMemoryUserRepositoryTest.initUser(UserRole.USER);
        Car car = InMemoryCarRepositoryTest.initCar(CarStatus.NEW);
        CarOrder order = initCarOrder(car, customer);

        CarOrder orderRes = carOrderRepository.save(order);

        assertNotNull(orderRes.getId());
        assertEquals(order.getCar(), orderRes.getCar());
        assertEquals(order.getCarOrderStatus(), orderRes.getCarOrderStatus());
        assertEquals(order.getDate(), orderRes.getDate());
        assertEquals(order.getCustomer(), orderRes.getCustomer());
    }

    @Test
    public void testFindById() {
        User customer = InMemoryUserRepositoryTest.initUser(UserRole.USER);
        Car car = InMemoryCarRepositoryTest.initCar(CarStatus.NEW);
        CarOrder order = initCarOrder(car, customer);
        order = carOrderRepository.save(order);

        Optional<CarOrder> orderResOpt = carOrderRepository.findById(order.getId());

        assertTrue(orderResOpt.isPresent());
        assertEquals(order.getId(), orderResOpt.get().getId());
    }

    @Test
    public void testContainsByCarAndStatuses() {
        User customer = InMemoryUserRepositoryTest.initUser(UserRole.USER);
        Car car = InMemoryCarRepositoryTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());
        CarOrder order = initCarOrder(car, customer);
        order = carOrderRepository.save(order);

        boolean res = carOrderRepository.containsByCarAndStatuses(car, List.of(order.getCarOrderStatus()));

        assertTrue(res);
    }

    @Test
    public void testFindByParams() {
        User customer = InMemoryUserRepositoryTest.initUser(UserRole.USER);
        customer.setId(UUID.randomUUID());
        Car car = InMemoryCarRepositoryTest.initCar(CarStatus.NEW);
        car.setId(UUID.randomUUID());
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
    }
}
