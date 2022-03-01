package com.thanhtam.backend.dto;

import lombok.Data;

@Data
public class CreateNewUser {

    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String roleName;
    
}
