package com.microsv.user_service.repository;


import com.microsv.user_service.entity.Role; // <-- Đảm bảo import đúng
import com.microsv.user_service.enumeration.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
}
