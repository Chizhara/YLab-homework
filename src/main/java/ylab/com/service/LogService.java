package ylab.com.service;

import lombok.RequiredArgsConstructor;
import ylab.com.exception.InvalidActionException;
import ylab.com.mapper.LogMapperImpl;
import ylab.com.model.Entity;
import ylab.com.model.car.Car;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.log.Log;
import ylab.com.model.log.LogEntityType;
import ylab.com.model.log.LogEventType;
import ylab.com.model.log.LogSearchParams;
import ylab.com.model.log.LogSearchRequest;
import ylab.com.model.order.CarOrder;
import ylab.com.model.user.User;
import ylab.com.repository.LogRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;
    private final UserService userService;
    private final LogMapperImpl logMapper;

    public Log save(User user, Entity entity, LogEventType eventType, ConsoleRequest request) {
        Log log = initLog(user, entity, eventType, request);
        return logRepository.save(log);
    }

    public List<Log> searchLogs(LogSearchRequest request) {
        LogSearchParams logSearchParams = logMapper.toLogSearchParams(request);

        if (request.getUserId() != null) {
            User user = userService.getUser(request.getUserId());
            logSearchParams.setUser(user);
        }

        return logRepository.findByParams(logSearchParams);
    }

    public void exportLogs(LogSearchRequest request) {
        LogSearchParams logSearchParams = logMapper.toLogSearchParams(request);

        if (request.getUserId() != null) {
            User user = userService.getUser(request.getUserId());
            logSearchParams.setUser(user);
        }
        List<Log> logs = logRepository.findByParams(logSearchParams);
        exportLogs(logs);
    }

    private void exportLogs(List<Log> logs) {
        final String fileName = "logs.txt";
        final String delimiter = " | ";
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, false))) {
            for(Log log : logs) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(log.getId()).append(delimiter)
                    .append(log.getUser().getId()).append(delimiter)
                    .append(log.getTimestamp()).append(delimiter)
                    .append(log.getEventType()).append(delimiter)
                    .append(log.getEntityType()).append(delimiter)
                    .append(log.getEntityId()).append(delimiter)
                    .append(log.getRequest().getHandlerKey().getMethod()).append(" ")
                    .append(log.getRequest().getHandlerKey().getPath()).append(delimiter)
                    .append(mapToString(log.getRequest().getParams())).append(delimiter)
                    .append(mapToString(log.getRequest().getRawObject())).append("\n");

                bufferedWriter.write(stringBuilder.toString());
            }

        } catch (IOException e) {
            throw new RuntimeException("Произошла неизвестная ошибка при попытке записи в файо");
        }
    }

    private String mapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> element : map.entrySet()) {
            stringBuilder
                .append(element.getKey())
                .append(" ")
                .append(element.getValue())
                .append("-");
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }

    private Log initLog(User user, Entity entity, LogEventType eventType, ConsoleRequest request) {
        return Log.builder()
            .user(user)
            .entityId(entity.getId())
            .entityType(getEntityType(entity))
            .eventType(eventType)
            .timestamp(Instant.now())
            .request(request)
            .build();
    }

    private LogEntityType getEntityType(Entity entity) {
        if (entity instanceof User) {
            return LogEntityType.USER;
        }
        if (entity instanceof CarOrder) {
            return LogEntityType.ORDER;
        }
        if (entity instanceof Car) {
            return LogEntityType.CAR;
        }
        throw new InvalidActionException("Неверно передан объект");
    }
}
