package ylab.com.model.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateRequest {
    private String login;
    private String password;
    private String email;
    private String phone;
}
