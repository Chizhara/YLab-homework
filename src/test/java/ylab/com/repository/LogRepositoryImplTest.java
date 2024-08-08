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
import ylab.com.mapper.LogMapperImpl;
import ylab.com.mapper.UserMapperImpl;
import ylab.com.model.Entity;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarStatus;
import ylab.com.model.log.Log;
import ylab.com.model.log.LogEntityType;
import ylab.com.model.log.LogEventType;
import ylab.com.model.log.LogSearchParams;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.repository.impl.CarRepositoryImpl;
import ylab.com.repository.impl.LogRepositoryImpl;
import ylab.com.repository.impl.UserRepositoryImpl;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LogRepositoryImplTest {
    private static int logIndex = 0;
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    ).withDatabaseName("test").withUsername("postgres").withPassword("root");
    private static LogRepositoryImpl logRepository;
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
        logRepository = new LogRepositoryImpl(connector, new LogMapperImpl());
        userRepository = new UserRepositoryImpl(connector, new UserMapperImpl());
        carRepository = new CarRepositoryImpl(connector, new CarMapperImpl());
        LiquibaseConfig.configure(connector);
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    public Log initLog(User user, LogEventType eventType, Entity entity, LogEntityType entityType) {
        return Log.builder()
            .user(user)
            .entityId(entity.getId())
            .entityType(entityType)
            .eventType(eventType)
            .timestamp(Instant.now())
            .request("")
            .build();
    }

    @Test
    @DisplayName("Сохранение информации о логах")
    public void test_shouldSave_whenCorrect() {
        User user = UserRepositoryImplTest.initUser(UserRole.MANAGER);
        Car car = CarRepositoryImplTest.initCar(CarStatus.NEW);
        carRepository.save(car);
        userRepository.save(user);
        Log log = initLog(user, LogEventType.UPDATE, car, LogEntityType.CAR);

        Log logRes = logRepository.save(log);

        assertNotNull(logRes.getId());
    }

    @Test
    @DisplayName("Поиск существующего лога по всем параметрам")
    public void test_shouldFindByAllParams_whenExists() {
        User user = UserRepositoryImplTest.initUser(UserRole.MANAGER);
        Car car = CarRepositoryImplTest.initCar(CarStatus.NEW);
        carRepository.save(car);
        userRepository.save(user);
        Log log = initLog(user, LogEventType.UPDATE, car, LogEntityType.CAR);

        LogSearchParams params = LogSearchParams.builder()
            .date(Date.from(log.getTimestamp()))
            .entityId(log.getEntityId())
            .eventType(log.getEventType())
            .entityType(log.getEntityType())
            .user(log.getUser())
            .build();


        Log logTemp = logRepository.save(log);
        List<Log> logsRes = logRepository.findByParams(params);

        assertEquals(1, logsRes.size());
        Log logRes = logsRes.get(0);
        assertEquals(logTemp.getId(), logRes.getId());
    }
}
