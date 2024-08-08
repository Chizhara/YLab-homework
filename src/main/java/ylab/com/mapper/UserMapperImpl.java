package ylab.com.mapper;

import ylab.com.model.user.User;
import ylab.com.model.user.dto.UserCreateRequest;
import ylab.com.model.user.UserOrderType;
import ylab.com.model.user.UserRole;
import ylab.com.model.user.UserSearchParams;
import ylab.com.model.user.dto.UserSearchRequest;
import ylab.com.model.user.dto.UserUpdateRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class UserMapperImpl {
    public User toUsers(UserCreateRequest userCreateRequest) {
        return User.builder()
                .login(userCreateRequest.getLogin())
                .password(userCreateRequest.getPassword())
                .role(userCreateRequest.getRole())
                .email(userCreateRequest.getEmail())
                .phone(userCreateRequest.getPhone())
                .build();
    }

    public User toUsers(User user, UserUpdateRequest userUpdateRequest) {
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

    public User toUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .login(rs.getString("login"))
                .password(rs.getString("password"))
                .role(UserRole.valueOf(rs.getString("role")))
                .email(rs.getString("email"))
                .phone(rs.getString("phone"))
                .build();
    }

    public List<User> toUsers(ResultSet rs) throws SQLException {
        List<User> users = new LinkedList<>();
        while (rs.next()) {
            users.add(toUser(rs));
        }
        return users;
    }
}
