package com.sc.zipdistance.service;

import com.sc.zipdistance.model.dto.CreateUserDto;
import com.sc.zipdistance.model.dto.UpdateUserDto;
import com.sc.zipdistance.model.dto.UserDto;
import com.sc.zipdistance.model.entity.User;
import com.sc.zipdistance.repository.UserRepository;
import com.sc.zipdistance.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * created 20/04/2025 - 12:52
 * project zipdistance
 * author sc
 */
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private UserService userService;

    private User user;
    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;
    private UserDto userDto;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPhoneNo("1234567890");
        user.setPasswordHash("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());

        createUserDto = new CreateUserDto();
        createUserDto.setEmail("test_create@example.com");
        createUserDto.setName("Test Create User");
        createUserDto.setPhoneNo("1234567890");
        createUserDto.setPassword("password123");
        createUserDto.setConfirmPassword("password123");

        updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail("test_update@example.com");
        updateUserDto.setName("Updated User");
        updateUserDto.setPhoneNo("0987654321");

        userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");
        userDto.setPhoneNo("1234567890");
    }

    // Tests for createUser
    @Test
    void testCreateUser_Success() {
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(auditLogService).log(eq("CREATE_USER"), eq(userId), eq("New user created"));

        UserDto result = userService.createUser(createUserDto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals("1234567890", result.getPhoneNo());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogService, times(1)).log(eq("CREATE_USER"), eq(userId), eq("New user created"));
    }

    @Test
    void testCreateUser_NullInput() {
        UserDto result = userService.createUser(null);

        assertNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(auditLogService, never()).log(anyString(), any(UUID.class), anyString());
    }

    @Test
    void testCreateUser_PasswordMismatch() {
        createUserDto.setConfirmPassword("differentPassword");

        UserDto result = userService.createUser(createUserDto);

        assertNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(auditLogService, never()).log(anyString(), any(UUID.class), anyString());
    }

    // Tests for updateUser
    @Test
    void testUpdateUser_Success() {
        when(userRepository.findByEmail("test_update@example.com")).thenReturn(Optional.of(user));

        User user2 = new User();
        user2.setId(userId);
        user2.setEmail(user.getEmail());
        user2.setName("Updated User");

        user2.setPhoneNo("9999");

        when(userRepository.save(any(User.class))).thenReturn(user2);
        doNothing().when(auditLogService).log(eq("UPDATE_USER"), eq(userId), eq("User updated"));

        UserDto result = userService.updateUser(updateUserDto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Updated User", result.getName()); // Name is updated but DTO reflects saved user
        assertEquals("9999", result.getPhoneNo());
        verify(userRepository, times(1)).findByEmail("test_update@example.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogService, times(1)).log(eq("UPDATE_USER"), eq(userId), eq("User updated"));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findByEmail("test_update@example.com")).thenReturn(Optional.empty());

        UserDto result = userService.updateUser(updateUserDto);

        assertNull(result);
        verify(userRepository, times(1)).findByEmail("test_update@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(auditLogService, never()).log(anyString(), any(UUID.class), anyString());
    }

    @Test
    void testUpdateUser_NullInput() {
        UserDto result = userService.updateUser(null);

        assertNull(result);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(auditLogService, never()).log(anyString(), any(UUID.class), anyString());
    }

    // Tests for getUsers
    @Test
    void testGetUsers_Success() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        assertEquals("Test User", result.get(0).getName());
        assertEquals("1234567890", result.get(0).getPhoneNo());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    // Tests for getUserById
    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1l);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals("1234567890", result.getPhoneNo());
        verify(userRepository, times(1)).findById(1l);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        UserDto result = userService.getUserById(1l);

        assertNull(result);
        verify(userRepository, times(1)).findById(1l);
    }

    @Test
    void testGetUserById_NullInput() {
        UserDto result = userService.getUserById(null);

        assertNull(result);
        verify(userRepository, times(1)).findById(null);
    }
}