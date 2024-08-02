package ylab.com.repository.impl;

import ylab.com.model.user.User;
import ylab.com.model.user.UserSearchParams;
import ylab.com.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class InMemoryUserRepository extends InMemoryRepository<UUID, User> implements UserRepository {
    @Override
    public User save(User user) {
        UUID id = UUID.randomUUID();
        user.setId(id);
        return super.save(id, user);
    }

    @Override
    public List<User> findAll() {
        return super.getAll().stream().toList();
    }

    public Optional<User> findById(UUID id) {
        return super.findByKey(id);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return super.getAll().stream()
            .filter(user ->
                user.getLogin().equals(login))
            .findFirst();
    }

    @Override
    public boolean containsUserWithLogin(String login) {
        return super.getAll().stream()
            .anyMatch(user ->
                user.getLogin().equals(login));
    }

    @Override
    public List<User> findAllByParams(UserSearchParams params) {
        Stream<User> stream = super.getAll().stream();

        if (params.getPhone() != null && !params.getPhone().isEmpty()) {
            stream = stream.filter(user -> user.getPhone().contains(params.getPhone()));
        }
        if (params.getEmail() != null && !params.getEmail().isEmpty()) {
            stream = stream.filter(user -> user.getEmail().contains(params.getEmail()));
        }
        if (params.getLogin() != null && !params.getLogin().isEmpty()) {
            stream = stream.filter(user -> user.getLogin().contains(params.getLogin()));
        }
        if (params.getRoles() != null && !params.getRoles().isEmpty()) {
            stream = stream.filter(user -> params.getRoles().contains(user.getRole()));
        }
        return stream.toList();
    }
}
