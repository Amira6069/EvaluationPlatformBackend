package com.governance.evaluation.controller;

import com.governance.evaluation.entity.User;
import com.governance.evaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get current user profile
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Don't send password
            Map<String, Object> userInfo = Map.of(
                "userId", user.getUserId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole().toString(),
                "isActive", user.getIsActive(),
                "createdAt", user.getCreatedAt()
            );

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            System.err.println("❌ Error fetching user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user"));
        }
    }

    /**
     * Update user profile
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> request) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update fields if provided
            if (request.containsKey("name") && request.get("name") != null) {
                user.setName(request.get("name"));
            }

            if (request.containsKey("email") && request.get("email") != null) {
                String newEmail = request.get("email");
                
                // Check if email is already taken by another user
                if (!newEmail.equals(user.getEmail())) {
                    if (userRepository.findByEmail(newEmail).isPresent()) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Email already in use"));
                    }
                    user.setEmail(newEmail);
                }
            }

            userRepository.save(user);

            System.out.println("✅ Profile updated for user: " + user.getEmail());

            return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "user", Map.of(
                    "userId", user.getUserId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole().toString()
                )
            ));

        } catch (Exception e) {
            System.err.println("❌ Error updating profile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update profile"));
        }
    }

    /**
     * Change password
     * POST /api/users/me/change-password
     */
    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Current password and new password are required"));
            }

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Current password is incorrect"));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            System.out.println("✅ Password changed for user: " + user.getEmail());

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));

        } catch (Exception e) {
            System.err.println("❌ Error changing password: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to change password"));
        }
    }
}