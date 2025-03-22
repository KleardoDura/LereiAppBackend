package com.lerei.store.security.respositories;

import java.util.Optional;

import com.lerei.store.security.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    boolean existsByPhoneNo(String phoneNo);
    Optional<User> findByEmail(String email);
    
}
