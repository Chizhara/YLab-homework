package ylab.com.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class User {
    private UUID id;
    private UserRole role;
    private String login;
    private String password;
}
