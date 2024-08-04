package ylab.com.service;

import lombok.RequiredArgsConstructor;
import ylab.com.exception.InvalidActionException;
import ylab.com.exception.NotFoundException;
import ylab.com.mapper.UserMapperImpl;
import ylab.com.model.user.User;
import ylab.com.model.user.UserCreateRequest;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.UserSearchParams;
import ylab.com.model.user.UserSearchRequest;
import ylab.com.model.user.UserUpdateRequest;
import ylab.com.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapper;

    public User getUser(UUID id) {
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
        User user = userMapper.toUser(request);
        user = userRepository.save(user);
        return user;
    }

    public User updateEmployer(UserUpdateRequest request, UUID employerId) {
        validateUpdateRequestCollisions(request);
        User searchedUser = getUser(employerId);
        if(searchedUser.getRole() == UserRole.USER) {
            throw new InvalidActionException("Нельзя обновлять информацию об обычном пользователе");
        }
        userMapper.toUser(searchedUser, request);
        return userRepository.save(searchedUser);
    }

    public User updateUser(User requester, UserUpdateRequest request, UUID userId) {
        UUID requesterId = requester.getId();
        validateUpdateRequest(requesterId, request, userId);
        User searchedUser = getUser(userId);
        userMapper.toUser(searchedUser, request);
        return userRepository.save(searchedUser);
    }

    private void validateUpdateRequest(UUID requesterId, UserUpdateRequest request, UUID userId) {
        if (requesterId != userId) {
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
