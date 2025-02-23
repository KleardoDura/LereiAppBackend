package com.lerei.store.security.controllers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.lerei.store.security.dtos.LoginUser;
import com.lerei.store.security.dtos.NewUser;
import com.lerei.store.security.entities.Role;
import com.lerei.store.security.enums.UserStatus;
import com.lerei.store.security.enums.UserType;
import com.lerei.store.security.jwt.JwtProvider;
import com.lerei.store.security.services.RoleService;
import com.lerei.store.security.services.UserService;
import com.lerei.store.security.util.CookieUtil;
import com.lerei.store.security.entities.User;
import com.lerei.store.security.enums.RoleList;

import com.lerei.store.services.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RoleService roleService;
    private final JwtProvider jwtProvider;
    private NewUser newUser;
    private String verificationCode;
    @Autowired
    private EmailSenderService senderService;
    @Value("${jwt.accessTokenCookieName}")
    private String cookieName;
    @Value("${host}")
    String hostFromValue;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
            UserService userService, RoleService roleService, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
        this.jwtProvider = jwtProvider;
    }
    @PostMapping("/login")
    public ResponseEntity<Object> login(HttpServletResponse httpServletResponse,
                                        @Valid @RequestBody LoginUser loginUser, BindingResult bidBindingResult, Locale locale){
        if(bidBindingResult.hasErrors()) {
            System.out.println(bidBindingResult);
            String message = messageSource.getMessage("invalid.credentials",null,locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        try {
            if(loginUser.getUserName()==null && loginUser.getEmail()!=null && !loginUser.getEmail().isEmpty()){
                if(!userService.existByEmail(loginUser.getEmail())) {
                    String message = messageSource.getMessage("email.not.registered", null, locale);
                    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
                }
                loginUser.setUserName(userService.getByEmail(loginUser.getEmail()).getUserName());
            }

                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginUser.getUserName(), loginUser.getPassword())
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtProvider.generateToken(authentication);
            CookieUtil.create(httpServletResponse, cookieName, jwt, false, -1, hostFromValue);
            userService.updateLastLoginTime(loginUser.getUserName());
            String message = messageSource.getMessage("login.success",null,locale);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            String message = messageSource.getMessage("invalid.credentials",null,locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthStatus() {
        // If the JWT is valid, this endpoint should be accessible and return 200 OK
        return ResponseEntity.ok("Authenticated");
    }

    @GetMapping("/details")
    public ResponseEntity<Object> getUserDetails(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        Optional<User> user= this.userService.getByUserName(userName);
        if (!user.isPresent())
            return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
        user.get().setPassword(null);//Do not return password, even though it's hashed
        return new ResponseEntity<>(user.get(), HttpStatus.OK) ;
    }
    @GetMapping("/logout")
    public ResponseEntity<?> logOut(HttpServletResponse httpServletResponse, Locale locale){
        CookieUtil.clear(httpServletResponse,cookieName);
        String message = messageSource.getMessage("logout",null,locale);
        return new ResponseEntity<>(message, HttpStatus.OK) ;
    }



    //Kete po perdorim per register, we were asked to keep it simple.. :)
    @PutMapping("/register-without-verification-code")
    public ResponseEntity<Object> register2(@Valid @RequestBody NewUser newUser, BindingResult bindingResult, Locale locale) {


        if (userService.existByEmail(newUser.getEmail())) {
            String message = messageSource.getMessage("email.already.registered", null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        if (userService.existByUserName(newUser.getUserName())) {
            String message = messageSource.getMessage("username.already.registered", null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByPhoneNo(newUser.getPhoneNo())) {
            String message = messageSource.getMessage("phone_no.already.registered", null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        System.out.println(bindingResult);
        if (bindingResult.hasErrors()) {
            String message = messageSource.getMessage("invalid.credentials",null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }







        User user = new User(newUser.getUserName(), newUser.getEmail(),
                passwordEncoder.encode(newUser.getPassword()));
        Set<Role> roles = new HashSet<>();
        /*
        //roles.add(roleService.getByRoleName(RoleList.ROLE_USER).get());
        Optional<Role> userRoleOptional = roleService.getByRoleName(RoleList.ROLE_USER);
        if (userRoleOptional.isPresent()) {
            roles.add(userRoleOptional.get());
        } else {//,,...
        }

        if (newUser.getRoles().contains("admin"))
            roles.add(roleService.getByRoleName(RoleList.ROLE_ADMIN).get());
        user.setRoles(roles);
        */
        roles.add(roleService.getByRoleName(RoleList.ROLE_USER).get());
        user.setRoles(roles);
        user.setPhoneNo(newUser.getPhoneNo());
        user.setRegisteredDate(new Timestamp(System.currentTimeMillis()));
        user.setStatus(UserStatus.ACTIVE);
        user.setType(UserType.INDIVIDUAL);

        userService.save(user);
        String message = messageSource.getMessage("register.success",null,locale);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }



    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody NewUser newUser, BindingResult bindingResult, Locale locale) {
        if (userService.existByEmail(newUser.getEmail())) {
            String message = messageSource.getMessage("email.already.registered", null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        if (userService.existByUserName(newUser.getUserName())) {
            String message = messageSource.getMessage("username.already.registered", null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        System.out.println(bindingResult);
        if (bindingResult.hasErrors()) {
            String message = messageSource.getMessage("invalid.credentials",null, locale);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        this.newUser=newUser;
        verificationCode=userService.generateVerificationCode();
        String bodyParams[]={newUser.getUserName(), verificationCode};
        String emailBody = messageSource.getMessage("verification.email.body",bodyParams, locale);
        //verification.email.subject
        String emailSubject = messageSource.getMessage("verification.email.subject", null,locale);
        senderService.sendEmail(newUser.getEmail(),emailSubject, emailBody);
        /*
        senderService.sendEmail(newUser.getEmail(),
                "Your Verification Code for Lerei Account",
                "Hello "+ newUser.getName() +",\n\n" +
                        "Your verification code is: " + verificationCode + "\n\n" +
                        "Please enter this code to verify your account and continue.\n\n" +
                        "Thank you,\n" +
                        "Lerei Music Store");
        */
        String params[] = {newUser.getEmail()};
        String message = messageSource.getMessage("verification.sent",params,locale);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }


    @PostMapping("/verify-and-register/{code}")
    public ResponseEntity<?> verifyAndRegister(@PathVariable String code, Locale locale){
        if(!verificationCode.equals(code)){
            String message = messageSource.getMessage("verification.code.mismatch",null,locale);
            return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
        }

        User user = new User(newUser.getUserName(), newUser.getEmail(),
                passwordEncoder.encode(newUser.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getByRoleName(RoleList.ROLE_USER).get());
        user.setRoles(roles);
        user.setPhoneNo(newUser.getPhoneNo());
        user.setRegisteredDate(new Timestamp(System.currentTimeMillis()));
        user.setStatus(UserStatus.ACTIVE);
        user.setType(UserType.INDIVIDUAL);

        userService.save(user);
        String message = messageSource.getMessage("register.success",null,locale);
        return new ResponseEntity<>(message, HttpStatus.CREATED);

    }

}
