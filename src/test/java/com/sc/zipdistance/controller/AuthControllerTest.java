package com.sc.zipdistance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sc.zipdistance.model.dto.LoginDto;
import com.sc.zipdistance.security.SecurityConfig;
import com.sc.zipdistance.service.UserDetailsServiceImpl;
import com.sc.zipdistance.service.UserService;
import com.sc.zipdistance.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * created 18/04/2025 - 16:54
 * project zipdistance
 * author sc
 */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private UserService userService; // Added to mock UserService
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testLogin_ReturnsJwtToken() throws Exception {
        String username = "admin@example.com";
        String password = "hashed_password1";
        String token = "mocked-jwt-token";

        LoginDto request = new LoginDto();
        request.setEmail(username);
        request.setPassword(password);
        request.setConfirmPassword(password);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(request);
        UserDetails userDetails = new User(username, password, new ArrayList<>());

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
// Mock UserService behavior

        mockMvc.perform(post("/api/v1/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(token));
    }

    @Test
    public void testLogin_Failed() throws Exception {
        String username = "admin@example.com";
        String password = "hashed_password1";
        String token = "mocked-jwt-token";

        LoginDto request = new LoginDto();
        request.setEmail(username);
        request.setPassword(password);
        request.setConfirmPassword(password);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(request);
        UserDetails userDetails = new User(username, password, new ArrayList<>());

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

}