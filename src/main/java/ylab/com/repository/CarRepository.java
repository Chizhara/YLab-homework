package ylab.com.repository;

import ylab.com.model.car.Car;
import ylab.com.model.car.CarSearchParams;

import java.util.List;
import java.util.Optional;

public interface CarRepository {
    /**
     * Сохраняет или обновляет информацию о машине.
     * Если id был пустой, ставит новый и сохраняет как новую сущность. Если id не пустой, обновляет существующую.
     *
     * @param car Информация и пользователе.
     * @return Результат сохранения машины.
     */
    Car save(Car car);

    /**
     * Поиск всех машин.
     *
     * @return Список всех машин.
     */
    List<Car> findAll();

    /**
     * Поиск машины по идентификатору.
     *
     * @param id идентификатор машины.
     * @return Результат поиска машины. Если машина не найден, возвращает пустой {@link Optional}.
     */
    Optional<Car> findById(Long id);

    /**
     * Удаление машины по идентификатору.
     *
     * @param id идентификатор машины.
     */
    void deleteById(Long id);

    /**
     * Поиск машин по параметрам
     *
     * @param params параметры фильтрации и сортировки машин.
     * @return Возвращает список машин, удовлетворяющих поисковым параметрам.
     */
    List<Car> findCarsByParams(CarSearchParams params);
}
