package com.governance.evaluation.controller;

import com.governance.evaluation.entity.User;
import com.governance.evaluation.entity.UserRole;
import com.governance.evaluation.repository.UserRepository;
import com.governance.evaluation.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            System.out.println("🔐 Login attempt for: " + email);

            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            System.out.println("✅ Authentication successful");

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            
            // ✅ CRITICAL: Use JwtService to generate token
            String token = jwtService.generateToken(userDetails);
            
            System.out.println("✅ Token generated: " + token.substring(0, 20) + "...");

            // Get user from database
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("email", user.getEmail());
            userInfo.put("name", user.getName());
            userInfo.put("role", user.getRole().toString());
            
            response.put("user", userInfo);

            System.out.println("✅ User logged in: " + email);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
        try {
            String email = registerRequest.get("email");
            String password = registerRequest.get("password");
            String name = registerRequest.get("name");
            String roleString = registerRequest.get("role");

            System.out.println("📝 Registration attempt for: " + email);

            // Check if user already exists
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email already registered"));
            }

            // Create new user
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setName(name);
            user.setRole(UserRole.valueOf(roleString.toUpperCase()));
            user.setIsActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);

            System.out.println("✅ User registered: " + email);

            // Generate token
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String token = jwtService.generateToken(userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", savedUser.getUserId());
            userInfo.put("email", savedUser.getEmail());
            userInfo.put("name", savedUser.getName());
            userInfo.put("role", savedUser.getRole().toString());
            
            response.put("user", userInfo);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Registration failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed"));
        }
    }
}