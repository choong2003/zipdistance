package com.sc.zipdistance.controller;

import com.sc.zipdistance.model.dto.BaseResponse;
import com.sc.zipdistance.model.dto.CreateUserDto;
import com.sc.zipdistance.model.dto.UpdateUserDto;
import com.sc.zipdistance.model.dto.UserDto;
import com.sc.zipdistance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Create a new user", description = "Requires 'CREATE_USER' authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "User creation failed")
    })
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

    @Operation(summary = "Update an existing user", description = "Requires 'UPDATE_USER' authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "User update failed")
    })
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

    @Operation(summary = "Get user by ID", description = "Requires 'VIEW_USERS' authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "User retrieval failed")
    })
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
