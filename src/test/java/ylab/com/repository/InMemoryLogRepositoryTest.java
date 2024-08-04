package ylab.com.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ylab.com.model.Entity;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarStatus;
import ylab.com.model.log.Log;
import ylab.com.model.log.LogEntityType;
import ylab.com.model.log.LogEventType;
import ylab.com.model.log.LogSearchParams;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.repository.impl.InMemoryLogRepository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryLogRepositoryTest {
    private static int logIndex = 0;
    private static LogRepository logRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        logRepository = new InMemoryLogRepository();
    }

    public Log initLog(User user, LogEventType eventType, Entity entity, LogEntityType entityType) {
        return Log.builder()
            .user(user)
            .entityId(entity.getId())
            .entityType(entityType)
            .eventType(eventType)
            .timestamp(Instant.now())
            .build();
    }

    @Test
    public void testAdd() {
        User user = InMemoryUserRepositoryTest.initUser(UserRole.MANAGER);
        Car car = InMemoryCarRepositoryTest.initCar(CarStatus.NEW);
        Log log = initLog(user, LogEventType.UPDATE, car, LogEntityType.CAR);

        Log logRes = logRepository.save(log);

        assertNotNull(logRes.getId());
    }

    @Test
    public void testSearch() {
        User user = InMemoryUserRepositoryTest.initUser(UserRole.MANAGER);
        Car car = InMemoryCarRepositoryTest.initCar(CarStatus.NEW);
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
