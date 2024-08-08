package ylab.com.repository;

import ylab.com.model.log.Log;
import ylab.com.model.log.LogSearchParams;

import java.util.List;

public interface LogRepository {
    /**
     * Сохраняет или обновляет информацию о логах.
     * Если id был пустой, ставит новый и сохраняет как новую сущность. Если id не пустой, обновляет существующую.
     *
     * @param log Информация и пользователе.
     * @return Результат сохранения логов.
     */
    Log save(Log log);
    /**
     * Поиск логов по параметрам
     *
     * @param params параметры фильтрации и сортировки логов.
     * @return Возвращает список логов, удовлетворяющих поисковым параметрам.
     */
    List<Log> findByParams(LogSearchParams params);
}
