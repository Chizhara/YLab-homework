package ylab.com.repository.impl;

import ylab.com.configure.Connector;
import ylab.com.exception.DatabaseException;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarSearchParams;
import ylab.com.repository.CarRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class CarRepositoryImpl extends CommonSequenceRepository implements CarRepository {
    private final static String sequence = "sys.car_sequence";
    private final CarMapperImpl carMapper;

    public CarRepositoryImpl(Connector connector, CarMapperImpl carMapper) {
        super(connector, sequence);
        this.carMapper = carMapper;
    }

    @Override
    public Car save(Car car) {
        try (Connection connection = openConnection()) {

            Long id = getNextLongSequence(connection);
            car.setId(id);

            String sql = "INSERT INTO develop.cars(id, brand, model, price, release_year, status, status_description) ";
            sql += String.format("VALUES (%s, '%s', '%s', %d, %d, '%s', '%s') ",
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getPrice(),
                    car.getReleaseYear().getValue(),
                    car.getStatus().toString(),
                    car.getStatusDescription());

            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
            return car;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<Car> findAll() {
        try (Connection connection = openConnection()) {
            String sql = "SELECT c.id, c.brand, c.model, c.price, c.release_year, c.status, c.status_description " +
                    "FROM develop.cars AS c ";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            return carMapper.toCars(rs);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<Car> findById(Long id) {
        try (Connection connection = openConnection()) {
            String sql = "SELECT c.id, c.brand, c.model, c.price, c.release_year, c.status, c.status_description " +
                    "FROM develop.cars AS c ";
            sql += String.format("WHERE c.id = %d", id);

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            List<Car> cars = carMapper.toCars(rs);
            if (cars.size() > 0) {
                return Optional.of(cars.get(0));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = openConnection()) {
            String sql = "DELETE FROM develop.cars AS c ";
            sql += String.format("WHERE c.id = %d", id);

            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<Car> findCarsByParams(CarSearchParams params) {
        try (Connection connection = openConnection()) {
            String sql = "SELECT c.id, c.brand, c.model, c.price, c.release_year, c.status, c.status_description " +
                    "FROM develop.cars AS c ";
            sql += "WHERE ";

            if (params.getBrand() != null && !params.getBrand().isEmpty()) {
                sql += String.format("c.brand ILIKE('%s%s%s') AND ", "%", params.getBrand(), "%");
            }
            if (params.getModel() != null && !params.getModel().isEmpty()) {
                sql += String.format("c.model ILIKE('%s%s%s') AND ", "%", params.getModel(), "%");
            }
            if (params.getStatus() != null) {
                sql += String.format("c.status = '%s' AND ", params.getStatus());
            }

            if (params.getPrice() != null) {
                if (params.getPrice().isLower()) {
                    sql += String.format("c.price <= %d ", params.getPrice().getValue());
                } else {
                    sql += String.format("c.price >= %d ", params.getPrice().getValue());
                }
            }

            if (sql.endsWith("WHERE ")) {
                sql = sql.substring(0, sql.lastIndexOf("WHERE"));
            }
            if (sql.endsWith("AND ")) {
                sql = sql.substring(0, sql.lastIndexOf("AND"));
            }
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            return carMapper.toCars(rs);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
