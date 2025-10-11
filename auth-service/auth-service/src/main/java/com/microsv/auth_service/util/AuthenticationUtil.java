package com.microsv.auth_service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final PasswordEncoder passwordEncoder;

    public void checkPassword(String rawPassword, String encodedPassword) {
        //sử dụng instance đã được tiêm vào, không tạo mới
        boolean checkPass = passwordEncoder.matches(rawPassword, encodedPassword);
        if (!checkPass) {
            throw new BadCredentialsException("Incorrect email or password");
        }
    }
}