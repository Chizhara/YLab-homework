package ylab.com.model.user.dto;

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
