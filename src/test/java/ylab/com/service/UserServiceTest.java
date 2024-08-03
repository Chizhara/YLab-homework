package ylab.com.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ylab.com.exception.InvalidActionException;
import ylab.com.mapper.UserMapperImpl;
import ylab.com.model.user.User;
import ylab.com.model.user.UserCreateRequest;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.UserSearchParams;
import ylab.com.model.user.UserSearchRequest;
import ylab.com.model.user.UserUpdateRequest;
import ylab.com.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTest {
    private static int userIndex = 0;
    private static User admin;
    private static UserService userService;
    private static UserMapperImpl userMapper;
    private static UserRepository userRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        userRepository = Mockito.mock(UserRepository.class);
        userMapper = new UserMapperImpl();
        userService = new UserService(userRepository, userMapper);

        admin = initUser(UserRole.ADMIN);
        admin.setId(UUID.randomUUID());
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
    public void testAddUser() {
        User user = initUser(UserRole.USER);
        UserCreateRequest request = UserCreateRequest.builder()
            .login(user.getLogin())
            .password(user.getPassword())
            .phone(user.getPhone())
            .email(user.getEmail())
            .role(user.getRole())
            .build();

        Mockito.when(userRepository.save(user)).thenReturn(user);
        User userRes = userService.addUser(request);

        assertEquals(user.getLogin(), userRes.getLogin());
        assertEquals(user.getPassword(), userRes.getPassword());
        assertEquals(user.getPhone(), userRes.getPhone());
        assertEquals(user.getEmail(), userRes.getEmail());
        assertEquals(user.getRole(), userRes.getRole());
    }

    @Test
    public void testUpdateUser() {
        User user = initUser(UserRole.USER);
        user.setId(UUID.randomUUID());
        UserUpdateRequest request = UserUpdateRequest.builder()
            .login(user.getLogin())
            .password(user.getPassword())
            .phone(user.getPhone())
            .email(user.getEmail())
            .build();

        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User userRes = userService.updateUser(user, request, user.getId());

        assertEquals(user.getLogin(), userRes.getLogin());
        assertEquals(user.getPassword(), userRes.getPassword());
        assertEquals(user.getPhone(), userRes.getPhone());
        assertEquals(user.getEmail(), userRes.getEmail());
        assertEquals(user.getRole(), userRes.getRole());
    }

    @Test
    public void testGetUser() {
        User user = initUser(UserRole.USER);
        user.setId(UUID.randomUUID());

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User userRes = userService.getUser(user.getId());

        assertEquals(user.getLogin(), userRes.getLogin());
        assertEquals(user.getPassword(), userRes.getPassword());
        assertEquals(user.getPhone(), userRes.getPhone());
        assertEquals(user.getEmail(), userRes.getEmail());
        assertEquals(user.getRole(), userRes.getRole());
    }

    @Test
    public void testSearchUsers() {
        User user = initUser(UserRole.USER);
        user.setId(UUID.randomUUID());

        UserSearchRequest request = UserSearchRequest.builder()
            .login(user.getLogin())
            .phone(user.getPhone())
            .roles(List.of(user.getRole()))
            .email(user.getEmail())
            .build();

        UserSearchParams params = userMapper.toUserSearchParams(request);

        Mockito.when(userRepository.findAllByParams(params)).thenReturn(List.of(user));

        List<User> usersRes = userService.findUsers(request, user);

        assertEquals(1, usersRes.size());

        User userRes = usersRes.get(0);

        assertEquals(user.getLogin(), userRes.getLogin());
        assertEquals(user.getPassword(), userRes.getPassword());
        assertEquals(user.getPhone(), userRes.getPhone());
        assertEquals(user.getEmail(), userRes.getEmail());
        assertEquals(user.getRole(), userRes.getRole());
    }

    @Test
    public void testUserSearchEmployers() {
        User user = initUser(UserRole.USER);
        user.setId(UUID.randomUUID());

        UserSearchRequest request = UserSearchRequest.builder()
            .roles(List.of(UserRole.ADMIN))
            .build();

        assertThrows(InvalidActionException.class, () -> userService.findUsers(request, user));
    }

    @Test
    public void testAdminSearchEmployers() {
        User user = initUser(UserRole.MANAGER);
        user.setId(UUID.randomUUID());

        UserSearchRequest request = UserSearchRequest.builder()
            .login(user.getLogin())
            .phone(user.getPhone())
            .roles(List.of(user.getRole()))
            .email(user.getEmail())
            .build();

        UserSearchParams params = userMapper.toUserSearchParams(request);

        Mockito.when(userRepository.findAllByParams(params)).thenReturn(List.of(user));

        List<User> usersRes = userService.findUsers(request, admin);

        assertEquals(1, usersRes.size());

        User userRes = usersRes.get(0);

        assertEquals(user.getLogin(), userRes.getLogin());
        assertEquals(user.getPassword(), userRes.getPassword());
        assertEquals(user.getPhone(), userRes.getPhone());
        assertEquals(user.getEmail(), userRes.getEmail());
        assertEquals(user.getRole(), userRes.getRole());
    }
}
