package com.sc.zipdistance.controller;

import com.sc.zipdistance.model.dto.LoginDto;
import com.sc.zipdistance.service.UserService;
import com.sc.zipdistance.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token if successful.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Login failed due to invalid credentials or input")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {

        if (loginDto != null && loginDto.getPassword().equalsIgnoreCase(loginDto.getConfirmPassword())) {
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.getEmail(),
                                loginDto.getPassword())
                );
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String token = jwtUtil.generateToken(userDetails);
                return ResponseEntity.ok(token);
            } catch (AuthenticationException ex) {
                return ResponseEntity.badRequest().build();
            } catch (Exception ex) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }
}
