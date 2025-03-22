package com.lerei.store.security.dtos;

import com.lerei.store.security.enums.UserStatus;
import com.lerei.store.security.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Getter
@Setter
@AllArgsConstructor
public class NewUser {

    @NotBlank(message = "Username cannot be empty")
    private String userName;
    @Email
    private String email;
    @NotBlank(message = "Password cannot be empty")
    private String password;

    private String phoneNo;
    private Timestamp registeredDate;
    private Timestamp lastLoginDate;
    private UserStatus status;
    private UserType type;
    private Set<String> roles = new HashSet<>();
    public String toString(){
        return "userName: "+userName+", email: "+email+ " ...";
    }
    public NewUser() {
    }
    /*
    public NewUser(@NotBlank String userName, @Email String email, @NotBlank String password,
            Set<String> roles) {

        this.userName = userName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
  */


    
}
