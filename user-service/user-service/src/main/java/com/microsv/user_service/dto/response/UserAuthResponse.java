package com.microsv.user_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class UserAuthResponse
{
    private Long userId;
    private String userName;
    private String email;
    private String profile;
    private String password;
    private Set<String> roles;
}
