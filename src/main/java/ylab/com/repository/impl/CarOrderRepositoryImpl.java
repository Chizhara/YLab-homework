package ylab.com.repository.impl;

import ylab.com.configure.Connector;
import ylab.com.exception.DatabaseException;
import ylab.com.mapper.CarMapperImpl;
import ylab.com.model.car.Car;
import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderSearchParams;
import ylab.com.model.order.CarOrderStatus;
import ylab.com.repository.CarOrderRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class CarOrderRepositoryImpl extends CommonSequenceRepository implements CarOrderRepository {
    private final static String sequence = "sys.order_sequence";
    private final CarMapperImpl carMapper;

    public CarOrderRepositoryImpl(Connector connector, CarMapperImpl carMapper) {
        super(connector, sequence);
        this.carMapper = carMapper;
    }

    @Override
    public CarOrder save(CarOrder order) {
        try (Connection connection = openConnection()) {

            Long id = getNextLongSequence(connection);
            order.setId(id);

            String sql = "INSERT INTO develop.orders(id, car_id, customer_id, status, date) ";
            sql += String.format("VALUES (%d, %d, %d, '%s', '%s')",
                order.getId(),
                order.getCar().getId(),
                order.getCustomer().getId(),
                order.getCarOrderStatus(),
                order.getDate());

            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
            return order;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<CarOrder> findById(Long id) {
        try (Connection connection = openConnection()) {
            String sql = "SELECT co.id, co.date, co.status AS order_status, " +
                "c.id, c.brand, c.model, c.price, c.release_year, c.status, c.status_description, " +
                "u.id AS user_id, u.login, u.password, u.role, u.email, u.phone " +
                "FROM develop.orders AS co " +
                "JOIN develop.cars AS c ON co.car_id = c.id " +
                "JOIN develop.users AS u ON co.customer_id = u.id ";
            sql += String.format("WHERE c.id = %d", id);

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            List<CarOrder> orders = carMapper.toCarOrders(rs);
            if (!orders.isEmpty()) {
                return Optional.of(orders.get(0));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public boolean containsByCarAndStatuses(Long carId, List<CarOrderStatus> orderStatuses) {
        try (Connection connection = openConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM develop.orders AS co ";
            sql += String.format("WHERE co.car_id = %d AND ", carId);
            sql += String.format("co.status IN (%s) ",
                String.join(", ", orderStatuses.stream()
                    .map(status -> String.format("'%s'", status.name())).toList()));

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            rs.next();
            long count = rs.getLong("count");
            return count > 0;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<CarOrder> findOrdersByParams(CarOrderSearchParams params) {
        try (Connection connection = openConnection()) {
            String sql = "SELECT co.id, co.date, co.status AS order_status, " +
                "c.id, c.brand, c.model, c.price, c.release_year, c.status, c.status_description, " +
                "u.id AS user_id, u.login, u.password, u.role, u.email, u.phone " +
                "FROM develop.orders AS co " +
                "JOIN develop.cars AS c ON co.car_id = c.id " +
                "JOIN develop.users AS u ON co.customer_id = u.id ";
            sql += "WHERE ";

            if (params.getCustomer() != null) {
                sql += String.format("u.id = %d AND ", params.getCustomer().getId());
            }
            if (params.getCar() != null) {
                sql += String.format("c.id = %d AND ", params.getCar().getId());
            }
            if (params.getStatus() != null) {
                sql += String.format("co.status IN (%s) AND ",
                    String.join(", ", params.getStatus().stream()
                        .map(status -> String.format("'%s'", status.name())).toList()));
            }
            if (params.getOrderDate() != null) {
                sql += String.format("co.date = '%s' AND ", params.getOrderDate());
            }

            if (sql.endsWith("WHERE ")) {
                sql = sql.substring(0, sql.lastIndexOf("WHERE"));
            }
            if (sql.endsWith("AND ")) {
                sql = sql.substring(0, sql.lastIndexOf("AND"));
            }

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            List<CarOrder> orders = carMapper.toCarOrders(rs);
            return orders;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
