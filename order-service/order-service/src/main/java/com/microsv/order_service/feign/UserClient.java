package com.microsv.order_service.feign;


import com.microsv.order_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name = "user-service" chính là spring.application.name của service kia đã đăng ký với Eureka
@FeignClient(name = "user-service", path = "/api/users")
public interface UserClient {

    // Chữ ký của phương thức này phải khớp với API bên user-service
    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}