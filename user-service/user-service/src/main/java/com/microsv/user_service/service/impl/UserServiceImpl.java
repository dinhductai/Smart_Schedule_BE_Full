package com.microsv.user_service.service.impl;

import com.microsv.user_service.dto.request.UserCreationRequest;
import com.microsv.user_service.dto.request.UserUpdateRequest;
import com.microsv.user_service.dto.response.UserAuthResponse;
import com.microsv.user_service.dto.response.UserResponse;
import com.microsv.user_service.entity.Role;
import com.microsv.user_service.entity.User;
import com.microsv.user_service.enumeration.RoleName;
import com.microsv.user_service.repository.PermissionRepository;
import com.microsv.user_service.repository.RoleRepository;
import com.microsv.user_service.repository.UserRepository;
import com.microsv.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    PasswordEncoder passwordEncoder;
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail())!=null) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    @Override
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new RuntimeException("Username exists");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setProfile(request.getProfile());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Gán vai trò USER mặc định
        Role userRole = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Default role 'USER' not found"));
        user.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);    }

    @Override
    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getProfile() != null) {
            user.setProfile(request.getProfile());
        }

        User updatedUser = userRepository.save(user);
        return toUserResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserAuthResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email) // <-- Sửa ở đây
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Build chuỗi scope từ Role và Permission
        Set<String> scopes = new HashSet<>();
        user.getRoles().forEach(role -> {
            scopes.add("ROLE_" + role.getRoleName().name());
            if (role.getPermissions() != null) {
                role.getPermissions().forEach(permission -> {
                    scopes.add(permission.getPermissionName());
                });
            }
        });


        return UserAuthResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .profile(user.getProfile())
                .password(user.getPassword())
                .roles(scopes)
                .build();
    }

    // hàm helper để chuyển đổi từ Entity sang DTO Response
    private UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName()); // Lấy từ userName
        response.setEmail(user.getEmail());
        response.setProfile(user.getProfile());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet()));
        return response;
    }


}
