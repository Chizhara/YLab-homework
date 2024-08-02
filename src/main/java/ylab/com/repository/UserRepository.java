package ylab.com.repository;

import ylab.com.model.user.User;
import ylab.com.model.user.UserSearchParams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    List<User> findAll();

    List<User> findAllByParams(UserSearchParams params);

    Optional<User> findById(UUID id);

    Optional<User> findByLogin(String login);

    boolean containsUserWithLogin(String login);
}
