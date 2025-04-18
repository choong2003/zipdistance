package com.sc.zipdistance.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserDto {

    @NotBlank(message = "Name must not be blank")
    private String name;
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "Phone Number must not be blank")
    private String phoneNo;
    @NotBlank(message = "Password must not be blank")
    private String password;
    @NotBlank(message = "Confirm Password must not be blank")
    private String confirmPassword;
}
