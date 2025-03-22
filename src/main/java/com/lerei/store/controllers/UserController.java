package com.lerei.store.controllers;

import com.lerei.store.dto.NewPasswordDto;
import com.lerei.store.dto.UpdateUserDto;
import com.lerei.store.security.dtos.NewUser;
import com.lerei.store.security.entities.Role;
import com.lerei.store.security.entities.User;
import com.lerei.store.security.enums.RoleList;
import com.lerei.store.security.enums.UserStatus;
import com.lerei.store.security.enums.UserType;
import com.lerei.store.security.respositories.UserRepository;
import com.lerei.store.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.springframework.web.bind.annotation.CrossOrigin;
@RestController
@CrossOrigin("*")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    public UserController(PasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
    }



    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UpdateUserDto newUser, BindingResult bindingResult, Locale locale) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();

        if (!user.getEmail().equals(newUser.getEmail()) && userService.existByEmail(newUser.getEmail())) {
            String message = messageSource.getMessage("email.already.registered", null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        if (!user.getUserName().equals(newUser.getUserName()) && userService.existByUserName(newUser.getUserName())) {
            String message = messageSource.getMessage("username.already.registered", null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (!user.getPhoneNo().equals(newUser.getPhoneNo()) && userService.existsByPhoneNo(newUser.getPhoneNo())) {
            String message = messageSource.getMessage("phone_no.already.registered", null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        System.out.println(bindingResult);
        if (bindingResult.hasErrors()) {
            String message = messageSource.getMessage("invalid.credentials",null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        user.setUserName(newUser.getUserName());
        user.setEmail(newUser.getEmail());
        user.setPhoneNo(newUser.getPhoneNo());
        userService.save(user);
        String message = messageSource.getMessage("update.success",null,locale);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody NewPasswordDto newPasswordDto, Locale locale){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();

        if (!passwordEncoder.matches(newPasswordDto.getPassword(), user.getPassword())) {
            String message = messageSource.getMessage("password.incorrect", null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }


        user.setPassword(passwordEncoder.encode(newPasswordDto.getNewPassword()));
        userRepository.save(user);
        String message = messageSource.getMessage("update.success",null,locale);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }


    @PutMapping("/update-email/{email}")
    public ResponseEntity<?> updateEmail(@PathVariable String email, Locale locale){
        if(userService.existByEmail(email)) {
            String message = messageSource.getMessage("email.already.registered",null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();
        user.setEmail(email);
        userRepository.save(user);
        String message = messageSource.getMessage("email.update.success",null, locale);
        return new ResponseEntity<>(message,HttpStatus.OK);

    }

    @PutMapping("/update-username/{newUsername}")
    public ResponseEntity<?> updateUsername(@PathVariable String newUsername, Locale locale){
        if(userService.existByUserName(newUsername)) {
            String message = messageSource.getMessage("username.already.registered",null, locale);
            return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();
        user.setUserName(newUsername);
        userRepository.save(user);
        String message = messageSource.getMessage("username.update.success",null, locale);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @PutMapping("/update-phone-no/{phoneNo}")
    public void updatePhoneNo(@PathVariable String phoneNo){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();
        user.setPhoneNo(phoneNo);
        userRepository.save(user);
    }



}
