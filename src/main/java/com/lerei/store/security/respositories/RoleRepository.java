package com.lerei.store.security.respositories;

import java.util.Optional;

import com.lerei.store.security.entities.Role;
import com.lerei.store.security.enums.RoleList;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository <Role, Integer> {
    Optional<Role> findByRoleName(RoleList roleName);
    
}
