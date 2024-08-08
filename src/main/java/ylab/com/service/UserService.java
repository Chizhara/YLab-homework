package ylab.com.service;

import lombok.RequiredArgsConstructor;
import ylab.com.exception.InvalidActionException;
import ylab.com.exception.NotFoundException;
import ylab.com.mapper.UserMapperImpl;
import ylab.com.model.user.User;
import ylab.com.model.user.dto.UserCreateRequest;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.UserSearchParams;
import ylab.com.model.user.dto.UserSearchRequest;
import ylab.com.model.user.dto.UserUpdateRequest;
import ylab.com.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapper;

    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(User.class, id));

    }

    public List<User> findUsers(UserSearchRequest userSearchRequest, User requester) {
        UserSearchParams userSearchParams = userMapper.toUserSearchParams(userSearchRequest);
        List<UserRole> userRoles = userSearchParams.getRoles();
        if (userRoles.contains(UserRole.ADMIN)
            || userRoles.contains(UserRole.MANAGER)
            && requester.getRole() != UserRole.ADMIN) {
            throw new InvalidActionException("Недостаточно прав для получения информации о сотрудниках");
        }
        return userRepository.findAllByParams(userSearchParams);
    }

    public User addUser(UserCreateRequest request) {
        validateLogin(request.getLogin());
        validateEmail(request.getEmail());
        validatePhone(request.getPhone());
        User user = userMapper.toUsers(request);
        user = userRepository.save(user);
        return user;
    }

    public User updateEmployer(UserUpdateRequest request, Long employerId) {
        validateUpdateRequestCollisions(request);
        User searchedUser = getUser(employerId);
        if(searchedUser.getRole() == UserRole.USER) {
            throw new InvalidActionException("Нельзя обновлять информацию об обычном пользователе");
        }
        userMapper.toUsers(searchedUser, request);
        return userRepository.save(searchedUser);
    }

    public User updateUser(User requester, UserUpdateRequest request, Long userId) {
        Long requesterId = requester.getId();
        validateUpdateRequest(requesterId, request, userId);
        User searchedUser = getUser(userId);
        userMapper.toUsers(searchedUser, request);
        return userRepository.save(searchedUser);
    }

    private void validateUpdateRequest(Long requesterId, UserUpdateRequest request, Long userId) {
        if (!Objects.equals(requesterId, userId)) {
            throw new InvalidActionException("Нельзя обновлять информацию о другом пользователе");
        }
        validateUpdateRequestCollisions(request);
    }

    private void validateUpdateRequestCollisions(UserUpdateRequest request) {
        if (request.getLogin() != null) {
            validateLogin(request.getLogin());
        }
        if (request.getPhone() != null) {
            validatePhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            validateEmail(request.getEmail());
        }
    }

    private void validateLogin(String login) {
        if (userRepository.containsUserWithLogin(login)) {
            throw new InvalidActionException("Требуется уникальный логин");
        }
    }

    private void validatePhone(String login) {
        if (userRepository.containsUserWithLogin(login)) {
            throw new InvalidActionException("Требуется уникальный логин");
        }
    }

    private void validateEmail(String login) {
        if (userRepository.containsUserWithLogin(login)) {
            throw new InvalidActionException("Требуется уникальный логин");
        }
    }

}
