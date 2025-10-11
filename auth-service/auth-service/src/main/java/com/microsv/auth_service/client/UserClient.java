package com.microsv.auth_service.client;

import com.microsv.auth_service.dto.response.UserAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service") // Tên của service cần gọi (đăng ký trên Eureka)
public interface UserClient {

    //khai báo phương thức khớp với API nội bộ của user-service
    @GetMapping("/internal/users/{email}")
    UserAuthResponse getUserAuthDetails(@PathVariable("email") String email);
}