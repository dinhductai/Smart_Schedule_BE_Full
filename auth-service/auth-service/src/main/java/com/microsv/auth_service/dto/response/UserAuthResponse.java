package com.microsv.auth_service.dto.response;
import lombok.Data;
import java.util.Set;

@Data
public class UserAuthResponse {
    private Long userId;
    private String userName;
    private String email;
    private String profile;
    private String password;
    private Set<String> roles;
}