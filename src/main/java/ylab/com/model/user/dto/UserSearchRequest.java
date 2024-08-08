package ylab.com.model.user.dto;

import lombok.Builder;
import lombok.Data;
import ylab.com.model.user.UserOrderType;
import ylab.com.model.user.UserRole;

import java.util.List;

@Data
@Builder
public class UserSearchRequest {
    private List<UserRole> roles;
    private String phone;
    private String email;
    private String login;
    private UserOrderType orderType;
    private Boolean desc;
}
