package ylab.com.repository;

import ylab.com.model.user.User;
import ylab.com.model.user.UserSearchParams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    /**
     * Сохраняет или обновляет информацию о пользователе.
     * Если id был пустой, ставит новый и сохраняет как новую сущность. Если id не пустой, обновляет существующую.
     *
     * @param user Информация и пользователе.
     * @return Результат сохранения пользователя.
     */
    User save(User user);

    /**
     * Поиск всех пользователей.
     *
     * @return Список всех пользователей.
     */
    List<User> findAll();

    /**
     * Поиск множества пользователей по параметрам
     *
     * @param params параметры фильтрации и сортировки пользователей.
     * @return Возвращает список пользователей, удовлетворяющих поисковым параметрам.
     */
    List<User> findAllByParams(UserSearchParams params);

    /**
     * Поиск пользователя по идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return Результат поиска пользователя. Если пользователь не найден, возвращает пустой {@link Optional}.
     */
    Optional<User> findById(Long id);

    /**
     * Поиск пользователя по логину.
     *
     * @param login логин пользователя.
     * @return Результат поиска пользователя. Если пользователь не найден, возвращает пустой {@link Optional}.
     */
    Optional<User> findByLogin(String login);

    /**
     * Проверяет факт существования пользователя с данным логином.
     *
     * @param login логин пользователя.
     * @return Возвращает true, если пользователь существует, false - если нет.
     */
    boolean containsUserWithLogin(String login);
}
