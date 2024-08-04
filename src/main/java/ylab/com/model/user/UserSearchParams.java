package ylab.com.model.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserSearchParams {
    private List<UserRole> roles;
    private String phone;
    private String email;
    private String login;
    private UserOrderType orderType;
    private Boolean desc;
}