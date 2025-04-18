package com.sc.zipdistance.model.dto;

import lombok.Data;

@Data
public class UpdateUserDto {
    private String name;
    private String email;
    private String phoneNo;
    private String password;
    private String confirmPassword;
}
