package com.microsv.auth_service.service.impl;
import com.microsv.auth_service.client.UserClient;
import com.microsv.auth_service.dto.request.*;
import com.microsv.auth_service.dto.response.*;
import com.microsv.auth_service.entity.InvalidatedToken;
import com.microsv.auth_service.exception.ExpiredJwtException;
import com.microsv.auth_service.repository.InvalidatedTokenRepository;
import com.microsv.auth_service.service.AuthenticationService;
import com.microsv.auth_service.util.AuthenticationUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    UserClient userClient;
    InvalidatedTokenRepository invalidatedTokenRepository;
    AuthenticationUtil authenticationUtil;

    @NonFinal
    @Value("${jwt.secret}")
    private String SIGNED_KEY;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws AuthenticationException, JOSEException {
        UserAuthResponse user = userClient.getUserAuthDetails(authenticationRequest.getEmail());
        authenticationUtil.checkPassword(authenticationRequest.getPassword(), user.getPassword());
        String scope = buildScope(user.getRoles());
        String token = generateToken(user,scope);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws AuthenticationException, JOSEException {
        String token = introspectRequest.getToken();
        boolean isInvalid = true;
        try {
            verifyToken(token);
        }catch (ExpiredJwtException e) {
            //nếu gặp trường hợp token đã bị logout thì chỉ cần đổi thành false là đc,ko cần trả exception
            isInvalid = false;
        }
        return IntrospectResponse.builder()
                //check token sau tg hiện tại và đc xác thực
                .valid(isInvalid)
                .build();    }

    @Override
    public void logout(LogoutRequest logoutRequest) throws AuthenticationException, ParseException {
        SignedJWT signedJWT = verifyToken(logoutRequest.getToken());
        //lấy claim set ra
        String jwtTokenId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtTokenId)
                .token(logoutRequest.getToken())
                .expiryTime(expirationTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
    }
    private SignedJWT verifyToken(String token) {
        try {
            JWSVerifier verifier = new MACVerifier(SIGNED_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);
            //kiểm tra xác thực
            boolean checkVerified = signedJWT.verify(verifier);
            //lấy thời gian hết hạn
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (!checkVerified && expiryTime.after(new Date())) {
                throw new ExpiredJwtException("Token expired");
            }else if (invalidatedTokenRepository.existsByToken(signedJWT.getJWTClaimsSet().getJWTID())){
                throw new ExpiredJwtException("Token expired");
            }else return signedJWT;

        } catch (JOSEException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
    private String generateToken(UserAuthResponse userAuthResponse,String scope) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS384);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(userAuthResponse.getUserId()))
                .issuer("smart_schedule.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("email",userAuthResponse.getEmail())
                .claim("scope",scope)
                .build();

        Payload payload =  new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);
        try{
            jwsObject.sign(new MACSigner(SIGNED_KEY.getBytes(StandardCharsets.UTF_8)));
            return jwsObject.serialize();
        }catch (JOSEException e){
            throw e;
        }
    }


    // cách này thì scope bị ngược
//    private String buildScope(Set<String> setRolePermission) {
//        // Nối tất cả các phần tử trong Set bằng một dấu cách " "
//        return String.join(" ", setRolePermission);
//    }

    private String buildScope(Set<String> setRolePermission) {
        // tạo hai danh sách riêng để lưu trữ roles và permissions
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        //phân loại các phần tử từ Set vào hai danh sách
        for (String scope : setRolePermission) {
            if (scope.startsWith("ROLE_")) {
                roles.add(scope);
            } else {
                permissions.add(scope);
            }
        }

        // sắp xếp từng danh sách để kết quả luôn nhất quán
        Collections.sort(roles);
        Collections.sort(permissions);

        // gộp hai danh sách lại, roles luôn ở trước
        List<String> orderedScopes = new ArrayList<>(roles);
        orderedScopes.addAll(permissions);

        // sử dụng logic StringBuilder của bạn trên danh sách đã được sắp xếp
        StringBuilder stringBuilder = new StringBuilder();
        for (String scope : orderedScopes) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(scope);
        }

        return stringBuilder.toString();
    }
}
