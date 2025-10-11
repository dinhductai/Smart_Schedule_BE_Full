package com.microsv.auth_service.controller;

import com.microsv.auth_service.dto.request.AuthenticationRequest;
import com.microsv.auth_service.dto.response.AuthenticationResponse;
import com.microsv.auth_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.naming.AuthenticationException;
import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        //gọi service để xác thực và tạo token
        AuthenticationResponse checkAuthenticate = null;
        try {
            checkAuthenticate = authenticationService.authenticate(request);
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(checkAuthenticate);
    }

}