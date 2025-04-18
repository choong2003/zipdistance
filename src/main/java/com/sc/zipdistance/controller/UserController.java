package com.sc.zipdistance.controller;

import com.sc.zipdistance.model.dto.BaseResponse;
import com.sc.zipdistance.model.dto.CreateUserDto;
import com.sc.zipdistance.model.dto.UpdateUserDto;
import com.sc.zipdistance.model.dto.UserDto;
import com.sc.zipdistance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('CREATE_USER')")
    @PostMapping
    public BaseResponse<UserDto> createUser(@RequestBody CreateUserDto createUser) {
        // Call the service to create a user
        if (createUser != null) {
            UserDto createdUser = userService.createUser(createUser);
            return new BaseResponse<>(createdUser, "SUCCESS", "User created successfully");
        }
        return new BaseResponse<UserDto>(null, "ERROR", "User creation failed");
    }

    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @PutMapping
    public BaseResponse<UserDto> updateUser(@RequestBody UpdateUserDto updateUser) {
        // Call the service to update a user
        if (updateUser != null) {
            UserDto updatedUser = userService.updateUser(updateUser);
            return new BaseResponse<>(updatedUser, "SUCCESS", "User updated successfully");
        }
        return new BaseResponse<UserDto>(null, "ERROR", "User update failed");
    }

    @PreAuthorize("hasAuthority('VIEW_USERS')")
    @GetMapping("/{id}")
    public BaseResponse<UserDto> getUserById(@PathVariable String id) {
        // Call the service to get user by id
        if (id != null) {
            UserDto user = userService.getUserById(Long.parseLong(id));
            return new BaseResponse<>(user, "SUCCESS", "User retrieved successfully");
        }
        return new BaseResponse<UserDto>(null, "ERROR", "User retrieval failed");
    }
}
