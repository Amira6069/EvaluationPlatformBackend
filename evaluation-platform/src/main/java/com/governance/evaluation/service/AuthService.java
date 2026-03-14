package com.governance.evaluation.service;

import com.governance.evaluation.entity.*;
import com.governance.evaluation.repository.UserRepository;
import com.governance.evaluation.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public Map<String, Object> register(String email, String password, String name, String role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
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
                throw new IllegalArgumentException("Invalid role");
        }
        
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setIsActive(true);
        
        userRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return response;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        User user = userOpt.get();
        
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("Account is inactive");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("email", user.getEmail());
        userInfo.put("name", user.getName());
        userInfo.put("role", user.getRole());
        
        response.put("user", userInfo);
        
        return response;
    }
}