package ylab.com.repository.impl;

import ylab.com.model.User;
import ylab.com.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class InMemoryUserRepository extends InMemoryRepository<UUID, User> implements UserRepository {
    @Override
    public User save(User user) {
        UUID id = UUID.randomUUID();
        user.setId(id);
        return super.save(id, user);
    }

    public Optional<User> findById(UUID id) {
        return super.findByKey(id);
    }
}
