package ylab.com.repository;

import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import ylab.com.configure.CommonConnector;
import ylab.com.configure.Connector;
import ylab.com.configure.LiquibaseConfig;
import ylab.com.mapper.UserMapperImpl;
import ylab.com.model.user.User;
import ylab.com.model.user.UserOrderType;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.UserSearchParams;
import ylab.com.repository.impl.UserRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRepositoryImplTest {
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    ).withDatabaseName("test").withUsername("postgres").withPassword("root");
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static int userIndex = 0;
    private static UserRepository userRepository;

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
        userRepository = new UserRepositoryImpl(connector, new UserMapperImpl());
        LiquibaseConfig.configure(connector);
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    public static User initUser(UserRole role) {
        userIndex++;
        return User.builder()
            .email("num_" + userIndex + "@gmail.com")
            .login("user_num_" + userIndex)
            .password("password_" + userIndex)
            .phone("899988877" + userIndex + userIndex)
            .role(role)
            .build();
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Сохранение корректной информации о пользователе")
    public void test_shouldSave_whenCorrect() {
        User user = initUser(UserRole.USER);
        User res = userRepository.save(user);

        assertNotNull(res.getId());
        assertEquals(user.getRole(), res.getRole());
        assertEquals(user.getPhone(), res.getPhone());
        assertEquals(user.getEmail(), res.getEmail());
        assertEquals(user.getPassword(), res.getPassword());
        assertEquals(user.getLogin(), res.getLogin());
    }

    @Test
    @DisplayName("Поиск существующего пользователя по логину")
    public void test_shouldFindUserByLogin_whenExists() {
        User user = initUser(UserRole.USER);
        user = userRepository.save(user);
        Optional<User> resOpt = userRepository.findByLogin(user.getLogin());

        assertTrue(resOpt.isPresent());

        User res = resOpt.get();

        assertEquals(user.getId(), res.getId());
    }

    @Test
    @DisplayName("Поиск существующего пользователя по идентификатору")
    public void test_shouldFindById_whenExists() {
        User user = initUser(UserRole.USER);
        user = userRepository.save(user);
        Optional<User> resOpt = userRepository.findById(user.getId());

        assertTrue(resOpt.isPresent());

        User res = resOpt.get();

        assertEquals(user.getId(), res.getId());
    }

    @Test
    @DisplayName("Поиск существующего пользователя по всем параметрам")
    public void test_shouldFindUsersByParams_whenExists() {
        User user = initUser(UserRole.USER);
        user = userRepository.save(user);

        UserSearchParams params = UserSearchParams.builder()
            .login(user.getLogin())
            .email(user.getEmail())
            .phone(user.getPhone())
            .roles(List.of(user.getRole()))
            .orderType(UserOrderType.LOGIN)
            .desc(false)
            .build();

        List<User> usersRes = userRepository.findAllByParams(params);

        assertEquals(1, usersRes.size());

        User userRes = usersRes.get(0);

        assertEquals(user.getId(), userRes.getId());
    }

    @Test
    @DisplayName("Проверка существования существующего пользователя по логину")
    public void test_shouldContainsUser_whenExists() {
        User user = initUser(UserRole.USER);
        user = userRepository.save(user);

        boolean res = userRepository.containsUserWithLogin(user.getLogin());

        assertTrue(res);
    }
}
