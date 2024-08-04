package ylab.com.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ylab.com.model.console.ConsoleRequest;
import ylab.com.model.security.Credentials;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.repository.UserRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Optional;

public class AuthServiceTest {

    private static AuthService authService;
    private static UserRepository userRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        userRepository = Mockito.mock(UserRepository.class);
        authService = new AuthService(userRepository);
    }

    @Test
    public void testLogin() {
        User user = UserServiceTest.initUser(UserRole.USER);

        Credentials credentials = Credentials.builder()
            .login(user.getLogin())
            .password(user.getPassword())
            .build();

        ConsoleRequest consoleRequest = new ConsoleRequest(null, null,
            Map.of("login", user.getLogin(),
                "password", user.getPassword()));

        Mockito.when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

        User userRes = authService.getUser(consoleRequest);

        assertEquals(user.getLogin(), userRes.getLogin());
        assertEquals(user.getPassword(), userRes.getPassword());
        assertEquals(user.getRole(), userRes.getRole());
    }
}
