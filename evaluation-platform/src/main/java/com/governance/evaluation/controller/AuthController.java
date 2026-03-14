package com.governance.evaluation.controller;

import com.governance.evaluation.entity.*;
import com.governance.evaluation.repository.UserRepository;
import com.governance.evaluation.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication Controller
 * Handles user registration and login
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String name = request.get("name");
            String role = request.get("role");

            // Validate inputs
            if (email == null || password == null || name == null || role == null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error", "All fields are required");
                return ResponseEntity.badRequest().body(errorMap);
            }

            // Check if email already exists
            if (userRepository.existsByEmail(email)) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error", "Email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
            }

            // Create user based on role
            User user;
            switch (role.toUpperCase()) {
                case "ORGANIZATION":
                    user = new Organization();
                    break;
                case "EVALUATOR":
                    user = new Evaluator();
                    break;
                case "ADMIN":
                    user = new Administrator();
                    break;
                default:
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("error", "Invalid role");
                    return ResponseEntity.badRequest().body(errorMap);
            }

            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setName(name);
            user.setIsActive(true);

            userRepository.save(user);

            System.out.println("✅ User registered: " + email + " (" + role + ")");

            Map<String, String> successMap = new HashMap<>();
            successMap.put("message", "User registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(successMap);

        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Registration failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }

    /**
     * Login user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            // Validate inputs
            if (email == null || password == null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error", "Email and password are required");
                return ResponseEntity.badRequest().body(errorMap);
            }

            // Find user
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (!optionalUser.isPresent()) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMap);
            }

            User user = optionalUser.get();

            // Check if user is active
            if (!user.getIsActive()) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error", "Account is inactive");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMap);
            }

            // Verify password
            if (!passwordEncoder.matches(password, user.getPassword())) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMap);
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail());

            System.out.println("✅ User logged in: " + email);

            // ✅ CRITICAL: Return COMPLETE user info
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("email", user.getEmail());
            userInfo.put("name", user.getName());
            userInfo.put("role", user.getRole().toString()); // ✅ Convert to String

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("token", token);
            responseMap.put("user", userInfo);

            return ResponseEntity.ok(responseMap);

        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Login failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }
}
