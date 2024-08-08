package ylab.com.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ylab.com.mapper.LogMapperImpl;
import ylab.com.model.Entity;
import ylab.com.model.car.Car;
import ylab.com.model.car.CarStatus;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.console.HandlerKey;
import ylab.com.model.console.Method;
import ylab.com.model.log.Log;
import ylab.com.model.log.LogEntityType;
import ylab.com.model.log.LogEventType;
import ylab.com.model.log.LogSearchParams;
import ylab.com.model.log.dto.LogSearchRequest;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.repository.LogRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogServiceTest {
    private static int logIndex = 0;
    private static LogService logService;
    private static LogRepository logRepository;
    private static UserService userService;
    private static LogMapperImpl logMapper;

    @BeforeAll
    public static void setUpBeforeClass() {
        logRepository = Mockito.mock(LogRepository.class);
        userService = Mockito.mock(UserService.class);
        logMapper = new LogMapperImpl();
        logService = new LogService(logRepository, userService, logMapper);
    }

    public static Log initLog(User user, LogEventType eventType, Entity entity, LogEntityType entityType) {
        logIndex++;
        return Log.builder()
            .id((long) logIndex++)
            .user(user)
            .entityId(entity.getId())
            .entityType(entityType)
            .eventType(eventType)
            .timestamp(Instant.now())
            .request("request ")
            .build();
    }

    @Test
    public void testAdd() {
        User user = UserServiceTest.initUser(UserRole.MANAGER);
        //user.setId(UUID.randomUUID());
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        //car.setId(UUID.randomUUID());
        Log log = initLog(user, LogEventType.UPDATE, car, LogEntityType.CAR);

        Mockito.when(logRepository.save(ArgumentMatchers.argThat(temp ->
                temp.getUser().getId().equals(user.getId())
                    && temp.getEntityType().equals(log.getEntityType())
                    && temp.getEntityId().equals(log.getEntityId())
                    && temp.getEventType().equals(log.getEventType()))))
            .thenReturn(log);

        ConsoleRequest consoleRequest = ConsoleRequest.builder()
            .handlerKey(HandlerKey.builder()
                .method(Method.PATCH)
                .path("/test/path")
                .build())
            .rawObject(Map.of("user", "iiii"))
            .params(Map.of("paramA", "bbbb"))
            .build();

        log.setRequest(logMapper.toString(consoleRequest));

        Log logRes = logService.save(user, car, LogEventType.UPDATE, consoleRequest);
        assertEquals(log, logRes);
    }

    @Test
    public void testSearch() {
        User user = UserServiceTest.initUser(UserRole.MANAGER);
        //user.setId(UUID.randomUUID());
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        //car.setId(UUID.randomUUID());
        Log log = initLog(user, LogEventType.UPDATE, car, LogEntityType.CAR);

        LogSearchRequest request = LogSearchRequest.builder()
            .date(Date.from(log.getTimestamp()))
            .userId(user.getId())
            .entityType(log.getEntityType())
            //  .entityId(log.getEntityId())
            .eventType(log.getEventType())
            .build();

        LogSearchParams params = logMapper.toLogSearchParams(request);


        Mockito.when(logRepository.findByParams(params)).thenReturn(List.of(log));

        List<Log> logsRes = logService.searchLogs(request);
        assertEquals(1, logsRes.size());
        Log logRes = logsRes.get(0);
        assertEquals(log.getId(), logRes.getId());
    }

    @Test
    public void test_shouldExportCorrectLogs_whenCorrect() {
        User user = UserServiceTest.initUser(UserRole.MANAGER);
        Car car = CarServiceTest.initCar(CarStatus.NEW);
        Log log = initLog(user, LogEventType.UPDATE, car, LogEntityType.CAR);

        ConsoleRequest consoleRequest = ConsoleRequest.builder()
            .handlerKey(HandlerKey.builder()
                .method(Method.PATCH)
                .path("/test/path")
                .build())
            .rawObject(Map.of("user", "iiii"))
            .params(Map.of("paramA", "bbbb"))
            .build();

        log.setRequest(logMapper.toString(consoleRequest));

        LogSearchRequest request = LogSearchRequest.builder()
            .date(Date.from(log.getTimestamp()))
            .userId(user.getId())
            .entityType(log.getEntityType())
            .entityId(log.getEntityId())
            .eventType(log.getEventType())
            .build();

        LogSearchParams params = logMapper.toLogSearchParams(request);

        Mockito.when(logRepository.findByParams(params)).thenReturn(List.of(log));

        logService.exportLogs(request);

        List<String> rawRes = new LinkedList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader("./logs.txt"))) {
            while (fileReader.ready()) {
                rawRes.add(fileReader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        assertEquals(1, rawRes.size());

        String[] res = rawRes.get(0).split(" \\| ");
        assertEquals(log.getId().toString(), res[0]);
        assertEquals(log.getUser().getId().toString(), res[1]);
        assertEquals(log.getTimestamp().toString(), res[2]);
        assertEquals(log.getEventType().toString(), res[3]);
        assertEquals(log.getEntityType().toString(), res[4]);
        assertEquals(log.getEntityId().toString(), res[5]);
        assertEquals(log.getRequest(), String.join(" | ", res[6], res[7], res[8]));
    }
}
