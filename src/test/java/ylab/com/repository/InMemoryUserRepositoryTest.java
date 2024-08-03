package ylab.com.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ylab.com.model.user.User;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.UserSearchParams;
import ylab.com.repository.impl.InMemoryUserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryUserRepositoryTest {
    private static int userIndex = 0;
    private static UserRepository userRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        userRepository = new InMemoryUserRepository();
    }

    public static User initUser(UserRole role) {
        userIndex++;
        return User.builder()
            .email("num_ " + userIndex + " +@gmail.com")
            .login("user_num_" + userIndex)
            .password("password_" + userIndex)
            .phone("899988877" + userIndex + userIndex)
            .role(role)
            .build();
    }

    @Test
    public void testSave() {
        User user = initUser(UserRole.USER);
        User res = userRepository.save(user);

        assertNotNull(res.getId());
        assertEquals(user.getRole(), res.getRole());
        assertEquals(user.getPhone(), res.getPhone());
        assertEquals(user.getEmail(), res.getEmail());
        assertEquals(user.getPassword(), res.getPassword());
        assertEquals(user.getLogin(), res.getLogin());
    }

    @Test
    public void testFindByLogin() {
        User user = initUser(UserRole.USER);
        user = userRepository.save(user);
        Optional<User> resOpt = userRepository.findByLogin(user.getLogin());

        assertTrue(resOpt.isPresent());

        User res = resOpt.get();

        assertNotNull(res.getId());
        assertEquals(user.getRole(), res.getRole());
        assertEquals(user.getPhone(), res.getPhone());
        assertEquals(user.getEmail(), res.getEmail());
        assertEquals(user.getPassword(), res.getPassword());
        assertEquals(user.getLogin(), res.getLogin());
    }

    @Test
    public void testFindById() {
        User user = initUser(UserRole.USER);
        user = userRepository.save(user);
        Optional<User> resOpt = userRepository.findById(user.getId());

        assertTrue(resOpt.isPresent());

        User res = resOpt.get();

        assertNotNull(res.getId());
        assertEquals(user.getRole(), res.getRole());
        assertEquals(user.getPhone(), res.getPhone());
        assertEquals(user.getEmail(), res.getEmail());
        assertEquals(user.getPassword(), res.getPassword());
        assertEquals(user.getLogin(), res.getLogin());
    }

    @Test
    public void testSearchByParams() {
        User user = initUser(UserRole.USER);
        user = userRepository.save(user);

        UserSearchParams params = UserSearchParams.builder()
            .login(user.getLogin())
            .email(user.getEmail())
            .phone(user.getPhone())
            .roles(List.of(user.getRole()))
            .build();

        List<User> usersRes = userRepository.findAllByParams(params);

        assertEquals(1, usersRes.size());

        User userRes = usersRes.get(0);

        assertNotNull(userRes.getId());
        assertEquals(user.getRole(), userRes.getRole());
        assertEquals(user.getPhone(), userRes.getPhone());
        assertEquals(user.getEmail(), userRes.getEmail());
        assertEquals(user.getPassword(), userRes.getPassword());
        assertEquals(user.getLogin(), userRes.getLogin());
    }

    @Test
    public void testContainsUser() {
        User user = initUser(UserRole.USER);
        user = userRepository.save(user);

        boolean res = userRepository.containsUserWithLogin(user.getLogin());

        assertTrue(res);
    }
}
