package ylab.com.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ylab.com.exception.InvalidActionException;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarCreateRequest;
import ylab.com.model.car.CarSearchParams;
import ylab.com.model.car.CarSearchRequest;
import ylab.com.model.car.CarStatus;
import ylab.com.model.car.CarUpdateRequest;
import ylab.com.model.order.CarOrderSearchParams;
import ylab.com.model.order.CarOrderStatus;
import ylab.com.model.user.User;
import ylab.com.repository.CarOrderRepository;
import ylab.com.repository.CarRepository;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CarServiceTest {
    private static int carIndex = 0;
    private static User admin;
    private static CarService carService;
    private static CarMapperImpl carMapper;
    private static CarRepository carRepository;
    private static CarOrderRepository carOrderRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        carRepository = Mockito.mock(CarRepository.class);
        carOrderRepository = Mockito.mock(CarOrderRepository.class);
        carMapper = new CarMapperImpl();
        carService = new CarService(carOrderRepository, carRepository, carMapper);
    }

    public static Car initCar(CarStatus carStatus) {
        carIndex++;
        return Car.builder()
            .id((long) carIndex)
            .brand("brand_" + carIndex)
            .model("model_" + carIndex)
            .price(1000 + carIndex)
            .releaseYear(Year.of(2020))
            .status(carStatus)
            .build();
    }

    @Test
    public void testAddCar() {
        Car car = initCar(CarStatus.NEW);
        car.setId(null);

        CarCreateRequest request = CarCreateRequest.builder()
            .brand(car.getBrand())
            .model(car.getModel())
            .price(car.getPrice())
            .releaseYear(car.getReleaseYear())
            .status(car.getStatus())
            .build();

        Mockito.when(carRepository.save(car)).thenReturn(car);
        Car carRes = carService.addCar(request);

        assertEquals(car.getBrand(), carRes.getBrand());
        assertEquals(car.getModel(), carRes.getModel());
        assertEquals(car.getPrice(), carRes.getPrice());
        assertEquals(car.getReleaseYear(), carRes.getReleaseYear());
        assertEquals(car.getStatusDescription(), carRes.getStatusDescription());
    }

    @Test
    public void testUpdateCar() {
        Car car = initCar(CarStatus.NEW);

        //car.setId(id);

        CarUpdateRequest request = CarUpdateRequest.builder()
            .price(car.getPrice() + 1)
            .status(CarStatus.MODIFIED)
            .build();

        Mockito.when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        car.setStatus(request.getStatus());
        car.setPrice(request.getPrice());

        List<CarOrderStatus> excludedStatuses = List.of(CarOrderStatus.CREATED, CarOrderStatus.CLOSED);

        Mockito.when(carOrderRepository.containsByCarAndStatuses(car.getId(), excludedStatuses)).thenReturn(false);
        Mockito.when(carRepository.save(car)).thenReturn(car);

        Car carRes = carService.updateCar(car.getId(), request);

        assertEquals(car.getId(), carRes.getId());
        assertEquals(car.getBrand(), carRes.getBrand());
        assertEquals(car.getModel(), carRes.getModel());
        assertEquals(car.getPrice(), carRes.getPrice());
        assertEquals(car.getReleaseYear(), carRes.getReleaseYear());
        assertEquals(car.getStatusDescription(), carRes.getStatusDescription());
    }

    @Test
    public void testRemoveCar() {
        Car car = initCar(CarStatus.NEW);

        CarUpdateRequest request = CarUpdateRequest.builder()
            .price(car.getPrice() + 1)
            .status(CarStatus.MODIFIED)
            .build();


        Mockito.when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        car.setStatus(request.getStatus());
        car.setPrice(request.getPrice());

        List<CarOrderStatus> excludedStatuses = List.of(CarOrderStatus.CREATED, CarOrderStatus.CLOSED);

        Mockito.when(carOrderRepository.containsByCarAndStatuses(car.getId(), excludedStatuses)).thenReturn(false);

        Car carRes = carService.removeCar(car.getId());

        assertEquals(car.getId(), carRes.getId());
    }

    @Test
    public void testFindCar() {
        Car car = initCar(CarStatus.NEW);

        UUID id = UUID.randomUUID();
        //car.setId(id);

        CarSearchRequest request = CarSearchRequest.builder()
            .status(car.getStatus())
            .priceParam(CarSearchRequest.PriceParam.builder()
                .lower(true)
                .value(car.getPrice() + 1)
                .build())
            .brand(car.getBrand())
            .model(car.getModel())
            .releaseYear(car.getReleaseYear())
            .build();

        CarSearchParams searchParams = carMapper.toCarSearchParams(request);

        Mockito.when(carOrderRepository.containsByCarAndStatuses(
                car.getId(),
                List.of(CarOrderStatus.CREATED, CarOrderStatus.CLOSED)))
            .thenReturn(false);

        Mockito.when(carRepository.findCarsByParams(searchParams)).thenReturn(List.of(car));

        List<Car> carsRes = carService.findCar(request);

        assertEquals(carsRes.size(), 1);

        Car carRes = carsRes.get(0);

        assertEquals(car.getId(), carRes.getId());
    }

    @Test
    public void testUpdateNotAvailableCar() {
        Car car = initCar(CarStatus.NEW);

        CarUpdateRequest request = CarUpdateRequest.builder()
            .price(car.getPrice() + 1)
            .status(CarStatus.MODIFIED)
            .build();


        Mockito.when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        car.setStatus(request.getStatus());
        car.setPrice(request.getPrice());

        List<CarOrderStatus> excludedStatuses = List.of(CarOrderStatus.CREATED, CarOrderStatus.CLOSED);

        Mockito.when(carOrderRepository.containsByCarAndStatuses(car.getId(), excludedStatuses)).thenReturn(true);

        assertThrows(InvalidActionException.class, () -> carService.removeCar(car.getId()));
    }
}
