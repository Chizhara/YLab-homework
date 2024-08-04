package ylab.com.model.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateRequest {
    private UserRole role;
    private String login;
    private String password;
    private String email;
    private String phone;
}
