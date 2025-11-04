package com.example.TaskApp.controller;

import com.example.TaskApp.dto.Response;
import com.example.TaskApp.dto.UserRequest;
import com.example.TaskApp.entity.User;
import com.example.TaskApp.repo.UserRepository;
import com.example.TaskApp.security.JwtUtils;
import com.example.TaskApp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin(
        origins = {
                "https://taskapp-frontend-8x0n.onrender.com"
        },
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},
        allowedHeaders = "*",
        allowCredentials = "true"
)
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<?>> register(@Valid @RequestBody UserRequest userRequest) {
        log.info("📝 Register endpoint hit for user: {}", userRequest.getUsername());
        Response<?> response = userService.signUp(userRequest);
        log.info("✅ Registration response: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        log.info("🔐 Login attempt for email: {}", email);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("❌ No user found for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("❌ Password mismatch for user: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        String token = jwtUtils.generateToken(email);
        log.info("✅ Token generated for user: {}", email);
        return ResponseEntity.ok(Map.of("data", token)); // ✅ matches frontend expectation
    }
}