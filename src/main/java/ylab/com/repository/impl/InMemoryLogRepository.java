package ylab.com.repository.impl;

import ylab.com.model.log.Log;
import ylab.com.model.log.LogSearchParams;
import ylab.com.repository.LogRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class InMemoryLogRepository extends InMemoryRepository<UUID, Log> implements LogRepository {

    @Override
    public Log save(Log log) {
        UUID id = UUID.randomUUID();
        log.setId(id);
        return super.save(id, log);
    }

    @Override
    public List<Log> findByParams(LogSearchParams params) {
        Stream<Log> logs = super.getAll().stream();

        if (params.getUser() != null) {
            logs = logs.filter(l -> l.getUser().equals(params.getUser()));
        }
        if (params.getEventType() != null) {
            logs = logs.filter(l -> l.getEventType().equals(params.getEventType()));
        }
        if (params.getEntityId() != null) {
            logs = logs.filter(l -> l.getEntityId().equals(params.getEntityId()));
        }
        if (params.getEntityType() != null) {
            logs = logs.filter(l -> l.getEntityType().equals(params.getEntityType()));
        }
        if(params.getDate() != null) {
            logs = logs.filter(log -> Date.from(log.getTimestamp()).equals(params.getDate()));
        }

        return logs.toList();
    }
}
