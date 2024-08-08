package ylab.com.model.user.dto;

import lombok.Builder;
import lombok.Data;
import ylab.com.model.user.UserRole;

@Data
@Builder
public class UserCreateRequest {
    private UserRole role;
    private String login;
    private String password;
    private String email;
    private String phone;
}
