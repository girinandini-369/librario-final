package com.librario.repository;

import com.librario.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
    Optional<Role> findByRoleNameIgnoreCase(String roleName);
}
