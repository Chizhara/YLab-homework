package ylab.com.model.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Credentials {
    private String login;
    private String password;
}
