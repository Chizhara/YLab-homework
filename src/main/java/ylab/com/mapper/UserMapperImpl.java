package ylab.com.mapper;

import ylab.com.model.user.User;
import ylab.com.model.user.UserCreateRequest;
import ylab.com.model.user.UserOrderType;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.UserSearchParams;
import ylab.com.model.user.UserSearchRequest;
import ylab.com.model.user.UserUpdateRequest;

import java.util.Map;
import java.util.stream.Stream;

public class UserMapperImpl {
    public User toUser(UserCreateRequest userCreateRequest) {
        return User.builder()
            .login(userCreateRequest.getLogin())
            .password(userCreateRequest.getPassword())
            .role(userCreateRequest.getRole())
            .email(userCreateRequest.getEmail())
            .phone(userCreateRequest.getPhone())
            .build();
    }

    public User toUser(User user, UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest.getLogin() != null) {
            user.setLogin(userUpdateRequest.getLogin());
        }
        if (userUpdateRequest.getPassword() != null) {
            user.setPassword(user.getPassword());
        }
        if (userUpdateRequest.getEmail() != null) {
            user.setEmail(userUpdateRequest.getEmail());
        }
        if (userUpdateRequest.getPhone() != null) {
            user.setPhone(userUpdateRequest.getPhone());
        }
        return user;
    }

    public UserCreateRequest toUserCreateRequest(Map<String, String> rawBody) {
        return UserCreateRequest.builder()
            .login(rawBody.get("login"))
            .role(UserRole.valueOf(rawBody.getOrDefault("role", "USER")))
            .password(rawBody.get("password"))
            .email(rawBody.get("email"))
            .phone(rawBody.get("phone"))
            .build();
    }

    public UserUpdateRequest toUserUpdateRequest(Map<String, String> rawBody) {
        return UserUpdateRequest.builder()
            .login(rawBody.get("login"))
            .password(rawBody.get("password"))
            .email(rawBody.get("email"))
            .phone(rawBody.get("phone"))
            .build();
    }

    public UserSearchParams toUserSearchParams(UserSearchRequest userSearchRequest) {
        return UserSearchParams.builder()
            .roles(userSearchRequest.getRoles())
            .email(userSearchRequest.getEmail())
            .phone(userSearchRequest.getPhone())
            .login(userSearchRequest.getLogin())
            .orderType(userSearchRequest.getOrderType())
            .desc(userSearchRequest.getDesc())
            .build();
    }

    public UserSearchRequest toUserSearchRequest(Map<String, String> rawBody) {
        return UserSearchRequest.builder()
            .roles(Stream.of(rawBody.get("roles").split(", ")).map(UserRole::valueOf).toList())
            .email(rawBody.get("email"))
            .login(rawBody.get("login"))
            .phone(rawBody.get("phone"))
            .orderType(UserOrderType.valueOf(rawBody.getOrDefault("orderType", "NONE")))
            .desc(Boolean.parseBoolean(rawBody.getOrDefault("desc", "false")))
            .build();
    }
}
