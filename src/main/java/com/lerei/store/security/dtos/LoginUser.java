package com.lerei.store.security.dtos;

import javax.validation.constraints.NotBlank;

public class LoginUser {
   // @NotBlank
    private String userName;
    private String email;
    @NotBlank
    private String password;
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
