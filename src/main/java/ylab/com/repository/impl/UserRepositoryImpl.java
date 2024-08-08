package ylab.com.repository.impl;

import ylab.com.configure.Connector;
import ylab.com.exception.DatabaseException;
import ylab.com.mapper.UserMapperImpl;
import ylab.com.model.user.User;
import ylab.com.model.user.UserSearchParams;
import ylab.com.repository.UserRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl extends CommonSequenceRepository implements UserRepository {

    private final static String sequence = "sys.user_sequence";
    private final UserMapperImpl userMapper;

    public UserRepositoryImpl(Connector connector, UserMapperImpl userMapper) {
        super(connector, sequence);
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        try (Connection connection = openConnection()) {

            Long id = getNextLongSequence(connection);
            user.setId(id);

            String sql = "INSERT INTO develop.users(id, login, password, role, email, phone) ";
            sql += String.format("VALUES (%s, '%s', '%s', '%s', '%s', '%s') ",
                user.getId(),
                user.getLogin(),
                user.getPassword(),
                user.getRole().toString(),
                user.getEmail(),
                user.getPhone());

            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        try (Connection connection = openConnection()) {
            String sql = "SELECT u.id AS user_id, u.login, u.password, u.role, u.email, u.phone FROM develop.users AS u";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            List<User> users = userMapper.toUsers(rs);
            return users;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<User> findAllByParams(UserSearchParams params) {
        try (Connection connection = openConnection()) {
            String sql = "SELECT u.id AS user_id, u.login, u.password, u.role, u.email, u.phone FROM develop.users AS u ";
            sql += "WHERE ";
            if (params.getPhone() != null && !params.getPhone().isEmpty()) {
                sql += String.format("u.phone ilike('%s') AND ", params.getPhone());
            }
            if (params.getEmail() != null && !params.getEmail().isEmpty()) {
                sql += String.format("u.email ilike('%s') AND ", params.getEmail());
            }
            if (params.getLogin() != null && !params.getLogin().isEmpty()) {
                sql += String.format("u.login ilike('%s') AND ", params.getLogin());
            }
            if (params.getRoles() != null && !params.getRoles().isEmpty()) {
                sql += String.format("u.email ilike('%s') ", params.getEmail());
            }

            if(sql.endsWith("WHERE ")) {
                sql = sql.substring(0, sql.lastIndexOf("WHERE"));
            }
            if(sql.endsWith("AND ")) {
                sql = sql.substring(0, sql.lastIndexOf("AND"));
            }

            switch (params.getOrderType()) {
                case ROLE ->
                    sql += " ORDER BY u.role " + (params.getDesc() ? " DESC" : " ASC" );
                case LOGIN ->
                    sql += " ORDER BY u.login " + (params.getDesc() ? " DESC" : " ASC" );
            }

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            List<User> users = userMapper.toUsers(rs);
            return users;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Connection connection = openConnection()) {
            String sql = "SELECT u.id AS user_id, u.login, u.password, u.role, u.email, u.phone FROM develop.users AS u ";
            sql += String.format("WHERE u.id = %s", id.toString());

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            List<User> users = userMapper.toUsers(rs);
            return Optional.ofNullable(users.get(0));
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        try (Connection connection = openConnection()) {
            String sql = "SELECT u.id AS user_id, u.login, u.password, u.role, u.email, u.phone FROM develop.users AS u ";
            sql += String.format("WHERE u.login = '%s'", login);

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            List<User> users = userMapper.toUsers(rs);
            return Optional.ofNullable(users.get(0));
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public boolean containsUserWithLogin(String login) {
        try (Connection connection = openConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM develop.users AS u ";
            sql += String.format("WHERE u.login = '%s'", login);

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            rs.next();
            long count = rs.getLong("count");
            return count > 0;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
