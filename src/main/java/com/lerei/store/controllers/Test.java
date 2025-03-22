package com.lerei.store.controllers;


import com.lerei.store.services.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.Locale;

@RestController
@CrossOrigin("*")
public class Test {
    @GetMapping("/testUser")
    @PreAuthorize("hasRole('USER')")
    public String testUserAccess(){
        return "HelloUser";
    }

    @GetMapping("/testAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public String testAdminAccess(){
        return "HelloAdmin";
    }

    //@GetMapping("/test") // aksesohet nga cdo user i loguar
    public String test(){

        try {
            sendMail();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "Hello";
    }
    public void sendMail() throws MessagingException {
        senderService.sendEmail("kleardodura13@gmail.com",
                "This is email subject",
                "This is email body");
    }
    @Autowired
    private EmailSenderService senderService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/test")
    public ResponseEntity<String> test2(Locale locale) {
        String params[] =  {"Kleo"};
        String message = messageSource.getMessage("email.sent",params, locale);
        return ResponseEntity.ok(message);
    }
}
