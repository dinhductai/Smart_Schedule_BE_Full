package com.microsv.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {


    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password; // Cho phép null nếu không muốn đổi pass

    private String userName;

    @Email
    private String email;

    private String profile;
}