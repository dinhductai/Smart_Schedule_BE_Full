package com.microsv.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @Email(message = "Invalid email")
    @NotBlank(message = "Email cannot be blank")
    String email;
    @NotBlank(message = "Password cannot be black")
    String password;
}