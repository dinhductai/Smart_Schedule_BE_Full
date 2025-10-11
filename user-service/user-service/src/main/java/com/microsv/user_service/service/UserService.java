package com.microsv.user_service.service;



import com.microsv.user_service.dto.request.UserCreationRequest;
import com.microsv.user_service.dto.request.UserUpdateRequest;
import com.microsv.user_service.dto.response.UserAuthResponse;
import com.microsv.user_service.dto.response.UserResponse;
import com.microsv.user_service.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User createUser(User user);
    UserResponse createUser(UserCreationRequest request);
    UserResponse getUser(Long userId);
    UserResponse updateUser(Long userId, UserUpdateRequest request);
    void deleteUser(Long userId);
    UserAuthResponse getUserByEmail(String email);
}
