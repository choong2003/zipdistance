package com.sc.zipdistance.model.dto;

import lombok.Data;

@Data
public class LoginDto {

    private String email;
    private String password;
    private String confirmPassword;
}
