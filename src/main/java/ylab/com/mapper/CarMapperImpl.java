package ylab.com.mapper;

import ylab.com.model.car.Car;
import ylab.com.model.car.CarCreateRequest;
import ylab.com.model.car.CarSearchParams;
import ylab.com.model.car.CarSearchRequest;
import ylab.com.model.car.CarStatus;
import ylab.com.model.car.CarUpdateRequest;
import ylab.com.model.order.*;
import ylab.com.model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.Year;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CarMapperImpl {
    private final UserMapperImpl userMapper = new UserMapperImpl();

    public Car toCar(CarCreateRequest request) {
        return Car.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .price(request.getPrice())
                .releaseYear(request.getReleaseYear())
                .status(request.getStatus())
                .statusDescription(request.getStatusDescription())
                .build();
    }

    public Car toCar(Car car, CarUpdateRequest request) {
        if (request.getPrice() != null) {
            car.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            car.setStatus(request.getStatus());
        }
        if (request.getStatusDescription() != null) {
            car.setStatusDescription(request.getStatusDescription());
        }
        return car;
    }

    public CarSearchParams toCarSearchParams(CarSearchRequest request) {
        CarSearchParams.CarSearchParamsBuilder searchParamsBuilder = CarSearchParams.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .releaseYear(request.getReleaseYear())
                .status(request.getStatus());

        if (request.getPriceParam() != null) {
            CarSearchRequest.PriceParam priceRequest = request.getPriceParam();
            searchParamsBuilder
                    .price(CarSearchParams.PriceParam.builder()
                            .value(priceRequest.getValue())
                            .lower(priceRequest.isLower())
                            .build());
        }
        return searchParamsBuilder.build();
    }

    public CarOrderSearchParams carToCarOrderSearchParams(CarOrderSearchRequest request, User customer, Car car) {
        return CarOrderSearchParams.builder()
                .car(car)
                .customer(customer)
                .orderDate(request.getOrderDate())
                .status(request.getStatus())
                .build();
    }

    public CarCreateRequest toCarCreateRequest(Map<String, String> rawBody) {
        return CarCreateRequest.builder()
                .brand(rawBody.get("brand"))
                .model(rawBody.get("model"))
                .price(Integer.parseInt(rawBody.get("price")))
                .releaseYear(Year.parse(rawBody.get("releaseYear")))
                .status(CarStatus.valueOf(rawBody.get("status")))
                .statusDescription(rawBody.get("statusDescription"))
                .build();
    }

    public CarUpdateRequest toCarUpdateRequest(Map<String, String> rawBody) {
        return CarUpdateRequest.builder()
                .price(Integer.parseInt(rawBody.get("price")))
                .status(CarStatus.valueOf(rawBody.get("status")))
                .statusDescription(rawBody.get("statusDescription"))
                .build();
    }

    public CarSearchRequest toCarSearchRequest(Map<String, String> rawBody) {
        return CarSearchRequest.builder()
                .brand(rawBody.get("brand"))
                .model(rawBody.get("model"))
                .priceParam(CarSearchRequest.PriceParam.builder()
                        .value(Integer.parseInt(rawBody.get("price value")))
                        .lower(Boolean.parseBoolean(rawBody.get("price lower")))
                        .build())
                .releaseYear(Year.parse(rawBody.get("releaseYear")))
                .status(CarStatus.valueOf(rawBody.get("status")))
                .build();
    }

    public CarOrderCreateRequest toCarOrderCreateRequest(Map<String, String> rawBody) {
        return CarOrderCreateRequest.builder()
                .carId(Long.parseLong(rawBody.get("car")))
                .build();
    }

    public CarOrderUpdateRequest toCarOrderUpdateRequest(Map<String, String> rawBody) {
        return CarOrderUpdateRequest.builder()
                .carOrderStatus(CarOrderStatus.valueOf(rawBody.get("status")))
                .build();
    }

    public CarOrderSearchRequest toCarOrderSearchRequest(Map<String, String> rawBody) {
        CarOrderSearchRequest.CarOrderSearchRequestBuilder orderSearchRequestBuilder = CarOrderSearchRequest.builder();

        if (rawBody.get("car") != null && !rawBody.get("car").isBlank()) {
            orderSearchRequestBuilder.carId(Long.parseLong(rawBody.get("car")));
        }

        if (rawBody.get("customer") != null && !rawBody.get("customer").isBlank()) {
            orderSearchRequestBuilder.customerId(Long.parseLong(rawBody.get("customer")));
        }

        if (rawBody.get("status") != null && !rawBody.get("status").isBlank()) {
            orderSearchRequestBuilder.status(Stream.of(rawBody.get("status").split(",")).map(CarOrderStatus::valueOf).toList());
        }

        if (rawBody.get("orderDate") != null && !rawBody.get("orderDate").isBlank()) {
            orderSearchRequestBuilder.orderDate(Instant.parse(rawBody.get("orderDate")));
        }

        return orderSearchRequestBuilder.build();
    }

    public Car toCar(ResultSet rs) throws SQLException {
        return Car.builder()
                .id(rs.getLong("id"))
                .brand(rs.getString("brand"))
                .model(rs.getString("model"))
                .price(rs.getInt("price"))
                .releaseYear(Year.of(rs.getObject("release_year", Integer.class)))
                .status(CarStatus.valueOf(rs.getString("status")))
                .statusDescription(rs.getString("status_description"))
                .build();
    }

    public List<Car> toCars(ResultSet rs) throws SQLException {
        List<Car> cars = new LinkedList<>();
        while (rs.next()) {
            cars.add(toCar(rs));
        }
        return cars;
    }

    public List<CarOrder> toCarOrders(ResultSet rs) throws SQLException {
        List<CarOrder> orders = new LinkedList<>();
        while (rs.next()) {
            orders.add(CarOrder.builder()
                    .id(rs.getLong("id"))
                    .car(toCar(rs))
                    .customer(userMapper.toUser(rs))
                    .date(rs.getTimestamp("date").toInstant())
                    .carOrderStatus(CarOrderStatus.valueOf(rs.getString("order_status")))
                    .build());
        }
        return orders;
    }
}
