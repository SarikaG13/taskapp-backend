package com.example.TaskApp.service.impl;

import com.example.TaskApp.dto.Response;
import com.example.TaskApp.dto.UserRequest;
import com.example.TaskApp.entity.User;
import com.example.TaskApp.enums.Role;
import com.example.TaskApp.repo.UserRepository;
import com.example.TaskApp.security.JwtUtils;
import com.example.TaskApp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Response<?> signUp(UserRequest userRequest) {
        log.info("Inside signUp() for email: {}", userRequest.getEmail());

        Optional<User> existingEmail = userRepository.findByEmail(userRequest.getEmail());
        if (existingEmail.isPresent()) {
            return Response.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Email already registered")
                    .build();
        }

        Optional<User> existingUsername = userRepository.findByUsername(userRequest.getUsername());
        if (existingUsername.isPresent()) {
            return Response.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Username already taken")
                    .build();
        }

        User user = new User();
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole(Role.USER);
        user.setName(userRequest.getName());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        try {
            userRepository.save(user);
            return Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("User registered successfully")
                    .build();
        } catch (Exception e) {
            log.error("💥 Error during user registration", e);
            return Response.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Registration failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response<?> login(UserRequest userRequest) {
        log.info("Login attempt for email: {}", userRequest.getEmail());

        Optional<User> optionalUser = userRepository.findByEmail(userRequest.getEmail());
        if (optionalUser.isEmpty()) {
            log.warn("User not found for email: {}", userRequest.getEmail());
            return Response.builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("User not found")
                    .build();
        }

        User user = optionalUser.get();
        log.info("Encoded password from DB: {}", user.getPassword());
        boolean passwordMatch = passwordEncoder.matches(userRequest.getPassword(), user.getPassword());
        log.info("Password match result: {}", passwordMatch);

        if (!passwordMatch) {
            return Response.builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid password")
                    .build();
        }

        String token = jwtUtils.generateToken(user.getEmail());

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .data(token)
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.error("No authenticated user found in security context");
            throw new UsernameNotFoundException("No authenticated user found");
        }

        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}