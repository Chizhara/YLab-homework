package ylab.com.repository;

import ylab.com.model.log.Log;
import ylab.com.model.log.LogSearchParams;

import java.util.List;

public interface LogRepository {

    Log save(Log log);
    List<Log> findByParams(LogSearchParams params);
}
