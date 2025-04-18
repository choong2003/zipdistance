package com.sc.zipdistance.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sc.zipdistance.model.dto.CreateUserDto;
import com.sc.zipdistance.model.dto.UpdateUserDto;
import com.sc.zipdistance.model.dto.UserDto;
import com.sc.zipdistance.model.entity.User;
import com.sc.zipdistance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AuditLogService auditLogService;

    // Register a new user
    public UserDto createUser(CreateUserDto createUserDto) {

        if (createUserDto == null || (!createUserDto.getPassword()
                .equalsIgnoreCase(createUserDto.getConfirmPassword()))) {
            //TODO: added custom exception
            return null;
        }
        String hashedPassword = passwordEncoder.encode(createUserDto.getPassword());
        User user = new User();
        user.setEmail(createUserDto.getEmail());
        user.setName(createUserDto.getName());
        user.setPhoneNo(createUserDto.getPhoneNo());
        user.setPasswordHash(hashedPassword);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        //Audit logs the action
        auditLogService.log("CREATE_USER", savedUser.getId(), "New user created");

        return convertToDto(savedUser);
    }

    public UserDto updateUser(UpdateUserDto updateUserDto) {
        Optional<User> userOptional = userRepository.findByEmail(updateUserDto.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(updateUserDto.getName());
            user.setPhoneNo(updateUserDto.getPhoneNo());
            User updatedUser = userRepository.save(user);
            //Audit logs the action
            auditLogService.log("UPDATE_USER", updatedUser.getId(), "User updated");
            return convertToDto(updatedUser);
        }
        return null;
    }

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return convertToDto(userOptional.get());
        }
        return null;
    }

    // Authenticate user by verifying the password
    public Optional<User> authenticateUser(String email, String rawPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean isValid = passwordEncoder.matches(rawPassword, user.getPasswordHash());
            return isValid ? userOptional : Optional.empty();
        }
        return Optional.empty();
    }

    public UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNo(user.getPhoneNo());
        return userDto;
    }
}
