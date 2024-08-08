package ylab.com.repository;

import ylab.com.model.order.CarOrder;
import ylab.com.model.order.CarOrderSearchParams;
import ylab.com.model.order.CarOrderStatus;

import java.util.List;
import java.util.Optional;

public interface CarOrderRepository {
    /**
     * Сохраняет или обновляет информацию о заказе.
     * Если id был пустой, ставит новый и сохраняет как новую сущность. Если id не пустой, обновляет существующую.
     *
     * @param order Информация и заказе.
     * @return Результат сохранения заказа.
     */
    CarOrder save(CarOrder order);

    /**
     * Поиск заказа по идентификатору.
     *
     * @param id идентификатор заказа.
     * @return Результат поиска заказа. Если заказ не найден, возвращает пустой {@link Optional}.
     */
    Optional<CarOrder> findById(Long id);

    /**
     * Проверка на факт существования заказа по индетификатору машины и статусам заказа.
     *
     * @param carId идентификатор машины.
     * @param orderStatuses список возможных статусов для заказа.
     * @return Возвращает true, если заказ удовлетворящий условиям существует, false - если нет.
     */
    boolean containsByCarAndStatuses(Long carId, List<CarOrderStatus> orderStatuses);

    /**
     * Поиск заказов по параметрам
     *
     * @param params параметры фильтрации и сортировки заказов.
     * @return Возвращает список заказов, удовлетворяющих поисковым параметрам.
     */
    List<CarOrder> findOrdersByParams(CarOrderSearchParams params);
}
