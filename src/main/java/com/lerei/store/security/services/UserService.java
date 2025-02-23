package com.lerei.store.security.services;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Random;

import javax.transaction.Transactional;

import com.lerei.store.security.respositories.UserRepository;
import com.lerei.store.security.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getByUserName(String userName){
        return userRepository.findByUserName(userName);
    }
    public boolean existByUserName(String userName){
        return userRepository.existsByUserName(userName);
    }
    public void save(User user){
        userRepository.save(user);
    }
    public boolean existByEmail(String email){ return userRepository.existsByEmail(email); }
    public boolean existsByPhoneNo(String phoneNo){ return userRepository.existsByPhoneNo(phoneNo);}
    public User getByEmail(String email) {
        if(existByEmail(email))
            return userRepository.findByEmail(email).orElseThrow();
        else return null;
    }
    public void updateLastLoginTime(String username){
        User user=userRepository.findByUserName(username).orElseThrow();
        user.setLastLoginDate(new Timestamp(System.currentTimeMillis()));
    }
    public static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(code);
    }
}
