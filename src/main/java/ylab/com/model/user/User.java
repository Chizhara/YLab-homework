package ylab.com.model.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ylab.com.model.Entity;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class User implements Entity {
    private Long id;
    private UserRole role;
    private String email;
    private String phone;
    private String login;
    private String password;
}
