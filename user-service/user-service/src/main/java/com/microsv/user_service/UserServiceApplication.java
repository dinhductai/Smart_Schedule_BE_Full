package com.microsv.user_service;

import com.microsv.user_service.entity.*;
import com.microsv.user_service.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.microsv.user_service.entity.Permission;
import com.microsv.user_service.enumeration.RoleName;


import java.util.Set;

@SpringBootApplication
@EnableDiscoveryClient // đăng ký eureka
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
	@Bean
	@DependsOn("entityManagerFactory") // <-- THÊM DÒNG NÀY

	CommandLineRunner commandLineRunner(
			UserRepository userRepository,
			RoleRepository roleRepository,
			PermissionRepository permissionRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			// === 1. TẠO CÁC PERMISSION ===
			// Chỉ tạo nếu chưa có permission nào trong DB
			if (permissionRepository.count() == 0) {
				Permission createTask = new Permission();
				createTask.setPermissionName("CREATE_TASK");
				permissionRepository.save(createTask);

				Permission viewDashboard = new Permission();
				viewDashboard.setPermissionName("VIEW_DASHBOARD");
				permissionRepository.save(viewDashboard);

				Permission viewAdmin = new Permission();
				viewAdmin.setPermissionName("VIEW_ADMIN_PANEL");
				permissionRepository.save(viewAdmin);

				System.out.println("Đã khởi tạo các Permissions mẫu.");
			}

			// === 2. TẠO CÁC ROLE ===
			// Chỉ tạo nếu chưa có role nào trong DB
			// === 2. TẠO CÁC ROLE ===
			if (roleRepository.count() == 0) {
				// Lấy lại các permission vừa tạo
				Permission createTask = permissionRepository.findByPermissionName("CREATE_TASK").orElseThrow();
				Permission viewDashboard = permissionRepository.findByPermissionName("VIEW_DASHBOARD").orElseThrow();
				Permission viewAdmin = permissionRepository.findByPermissionName("VIEW_ADMIN_PANEL").orElseThrow();

				// Tạo role USER (Đã đúng)
				Role userRole = new Role();
				userRole.setRoleName(RoleName.USER);
				userRole.setPermissions(Set.of(createTask, viewDashboard));
				roleRepository.save(userRole);

				// Tạo role ADMIN (Sửa ở đây)
				Role adminRole = new Role();
				adminRole.setRoleName(RoleName.ADMIN);
				// THÊM "viewAdmin" VÀO ĐÂY
				adminRole.setPermissions(Set.of(createTask, viewDashboard, viewAdmin));
				roleRepository.save(adminRole);

				System.out.println("Đã khởi tạo các Roles mẫu.");
			}
			// === 3. TẠO CÁC USER ===
			// Chỉ tạo user "admin" nếu chưa tồn tại
			if (userRepository.findByUserName("admin").isEmpty()) {
				Role adminRole = (Role) roleRepository.findByRoleName(RoleName.ADMIN).orElseThrow();
				User admin = new User();
				admin.setUserName("admin");
				admin.setEmail("admin@gmail.com");
				admin.setPassword(passwordEncoder.encode("admin123")); // Mã hóa mật khẩu
				admin.setRoles(Set.of(adminRole));
				userRepository.save(admin);
				System.out.println("Đã tạo user 'admin'.");
			}

			// Chỉ tạo user "user" nếu chưa tồn tại
			if (userRepository.findByUserName("user").isEmpty()) {
				Role userRole = (Role) roleRepository.findByRoleName(RoleName.USER).orElseThrow();
				User user = new User();
				user.setUserName("user");
				user.setEmail("user2@gmail.com");
				user.setPassword(passwordEncoder.encode("user123")); // Mã hóa mật khẩu
				user.setRoles(Set.of(userRole));
				userRepository.save(user);
				System.out.println("Đã tạo user 'user'.");
			}
		};
	}
}
