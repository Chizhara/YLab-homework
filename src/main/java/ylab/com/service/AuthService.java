package ylab.com.service;

import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.security.Credentials;
import ylab.com.model.user.User;
import ylab.com.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(ConsoleRequest consoleRequest) {
        Credentials credentials = toCredentials(consoleRequest.getParams());
        return login(credentials);
    }

    private Credentials toCredentials(Map<String, String> rawCredentials) {
        return Credentials.builder()
            .login(rawCredentials.get("login"))
            .password(rawCredentials.get("password"))
            .build();
    }

    private User login(Credentials credentials) {
        Optional<User> userOpt = userRepository.findByLogin(credentials.getLogin());
        User user;
        if (userOpt.isEmpty()) {
            return null;
        }
        user = userOpt.get();

        if (user.getPassword().equals(credentials.getPassword())) {
            return user;
        }
        return null;
    }
}
