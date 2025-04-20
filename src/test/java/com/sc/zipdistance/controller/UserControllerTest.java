package com.sc.zipdistance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sc.zipdistance.model.dto.CreateUserDto;
import com.sc.zipdistance.model.dto.UserDto;
import com.sc.zipdistance.security.SecurityConfig;
import com.sc.zipdistance.service.UserService;
import com.sc.zipdistance.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * created 18/04/2025 - 22:07
 * project zipdistance
 * author sc
 */

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @InjectMocks
    private UserController userController;
    @Autowired
    private ObjectMapper objectMapper; // For JSON serialization
    private CreateUserDto createUserDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto();
        createUserDto.setEmail("test@example.com");
        createUserDto.setName("Test User");

        userDto = new UserDto();
        userDto.setId("1");
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");
    }

    // Positive Test Case: Successful user creation
    @Test
    @WithMockUser(authorities = "CREATE_USER")
    void testCreateUser_Success() throws Exception {
        // Arrange
        when(userService.createUser(any(CreateUserDto.class))).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/users") // Adjust the endpoint URL as needed
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.result.id").value("1"))
                .andExpect(jsonPath("$.result.email").value("test@example.com"))
                .andExpect(jsonPath("$.result.name").value("Test User"));

        verify(userService, times(1)).createUser(any(CreateUserDto.class));
    }

    // Negative Test Case: Fail when input is null
    @Test
    @WithMockUser(authorities = "CREATE_USER")
    void testCreateUser_Failure_NullInput() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/users") // Adjust the endpoint URL as needed
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")) // Simulate null or empty body
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        verify(userService, never()).createUser(any());
    }

    // Additional Test Case: Access denied without CREATE_USER authority
    @Test
    @WithMockUser(authorities = "VIEW_USER")
    void testCreateUser_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/users") // Adjust the endpoint URL as needed
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isForbidden()); // Expect 403 Forbidden for AccessDeniedException

        verify(userService, never()).createUser(any());
    }
}