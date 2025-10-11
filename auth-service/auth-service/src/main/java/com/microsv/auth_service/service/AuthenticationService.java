package com.microsv.auth_service.service;

import com.microsv.auth_service.dto.request.AuthenticationRequest;
import com.microsv.auth_service.dto.request.IntrospectRequest;
import com.microsv.auth_service.dto.request.LogoutRequest;
import com.microsv.auth_service.dto.response.AuthenticationResponse;
import com.microsv.auth_service.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import javax.naming.AuthenticationException;
import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws AuthenticationException, JOSEException;
    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws AuthenticationException,JOSEException;
    void logout(LogoutRequest logoutRequest) throws AuthenticationException, ParseException;
}
