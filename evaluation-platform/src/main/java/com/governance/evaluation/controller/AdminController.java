package com.governance.evaluation.controller;

import com.governance.evaluation.entity.*;
import com.governance.evaluation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EvaluationResultRepository resultRepository;

    @Autowired
    private EvaluationResponseRepository responseRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            System.out.println("📡 GET /api/admin/users - Fetching all users");
            
            List<User> allUsers = userRepository.findAll();
            
            List<Map<String, Object>> userDTOs = allUsers.stream()
                .map(user -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("userId", user.getUserId());
                    dto.put("name", user.getName());
                    dto.put("email", user.getEmail());
                    dto.put("role", user.getRole());
                    dto.put("active", user.getIsActive());
                    return dto;
                })
                .collect(Collectors.toList());
            
            System.out.println("✅ Found " + userDTOs.size() + " users");
            return ResponseEntity.ok(userDTOs);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching users: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch users"));
        }
    }

    /**
     * Create new user
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String name = request.get("name");
            String role = request.get("role");

            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Email already exists"));
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
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid role"));
            }

            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setName(name);
            user.setIsActive(true);

            userRepository.save(user);
            System.out.println("✅ User created: " + email);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User created successfully"));

        } catch (Exception e) {
            System.err.println("❌ Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create user"));
        }
    }

    /**
     * Update user
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            if (updates.containsKey("name")) {
                user.setName((String) updates.get("name"));
            }
            if (updates.containsKey("email")) {
                user.setEmail((String) updates.get("email"));
            }
            if (updates.containsKey("password")) {
                String pwd = (String) updates.get("password");
                if (pwd != null && !pwd.isEmpty()) {
                    user.setPassword(passwordEncoder.encode(pwd));
                }
            }
            if (updates.containsKey("active")) {
                user.setIsActive((Boolean) updates.get("active"));
            }

            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "User updated successfully"));

        } catch (Exception e) {
            System.err.println("❌ Error updating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user"));
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));

        } catch (Exception e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user"));
        }
    }

    /**
     * Toggle user status
     */
    @PatchMapping("/users/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            user.setIsActive(!user.getIsActive());
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "User status updated", "active", user.getIsActive()));

        } catch (Exception e) {
            System.err.println("❌ Error toggling status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to toggle status"));
        }
    }

    /**
     * Get all evaluations
     */
    @GetMapping("/evaluations")
    @Transactional
    public ResponseEntity<?> getAllEvaluations() {
        try {
            System.out.println("📡 GET /api/admin/evaluations");

            List<Evaluation> evaluations = evaluationRepository.findAll();

            List<Map<String, Object>> dtos = evaluations.stream()
                .map(eval -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("evaluationId", eval.getEvaluationId());
                    dto.put("name", eval.getName());
                    dto.put("description", eval.getDescription());
                    dto.put("period", eval.getPeriod());
                    dto.put("status", eval.getStatus().toString());
                    dto.put("totalScore", eval.getTotalScore());
                    dto.put("createdAt", eval.getCreatedAt());

                    if (eval.getOrganization() != null) {
                        dto.put("organizationId", eval.getOrganization().getUserId());
                        dto.put("organizationName", eval.getOrganization().getName());
                    }

                    return dto;
                })
                .collect(Collectors.toList());

            System.out.println("✅ Found " + evaluations.size() + " evaluations");
            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            System.err.println("❌ Error fetching evaluations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch evaluations"));
        }
    }

    /**
     * Get stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            long totalUsers = userRepository.count();
            long totalOrganizations = userRepository.findAll().stream()
                    .filter(u -> u instanceof Organization).count();
            long totalEvaluations = evaluationRepository.count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("totalOrganizations", totalOrganizations);
            stats.put("totalEvaluations", totalEvaluations);

            System.out.println("✅ Stats calculated successfully");
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            System.err.println("❌ Error calculating stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get stats"));
        }
    }

    /**
     * Approve evaluation
     */
    @PostMapping("/evaluations/{id}/approve")
    public ResponseEntity<?> approveEvaluation(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            evaluation.setStatus(EvaluationStatus.APPROVED);
            evaluationRepository.save(evaluation);

            generateEvaluationResult(evaluation);
            generateRecommendations(evaluation);

            System.out.println("✅ Admin approved evaluation: " + evaluation.getName());
            return ResponseEntity.ok(Map.of("message", "Evaluation approved successfully"));

        } catch (Exception e) {
            System.err.println("❌ Error approving evaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to approve evaluation"));
        }
    }

    /**
     * Reject evaluation
     */
    @PostMapping("/evaluations/{id}/reject")
    public ResponseEntity<?> rejectEvaluation(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            evaluation.setStatus(EvaluationStatus.REJECTED);
            evaluationRepository.save(evaluation);

            System.out.println("✅ Admin rejected evaluation: " + evaluation.getName());
            return ResponseEntity.ok(Map.of("message", "Evaluation rejected"));

        } catch (Exception e) {
            System.err.println("❌ Error rejecting evaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reject evaluation"));
        }
    }

    // Helper methods
    private void generateEvaluationResult(Evaluation evaluation) {
        Optional<EvaluationResult> existing = resultRepository.findByEvaluation_EvaluationId(evaluation.getEvaluationId());
        if (existing.isPresent()) return;

        Double score = evaluation.getTotalScore() != null ? evaluation.getTotalScore() : 0.0;
        String label;
        boolean certified;

        if (score >= 90) {
            label = "CERTIFIED_PLATINUM";
            certified = true;
        } else if (score >= 80) {
            label = "CERTIFIED_GOLD";
            certified = true;
        } else if (score >= 65) {
            label = "CERTIFIED_SILVER";
            certified = true;
        } else if (score >= 50) {
            label = "CERTIFIED_BRONZE";
            certified = true;
        } else {
            label = "NOT_CERTIFIED";
            certified = false;
        }

        EvaluationResult result = new EvaluationResult();
        result.setEvaluation(evaluation);
        result.setFinalScore(score);
        result.setCertificationLabel(label);
        result.setIsCertified(certified);
        if (certified) {
            result.setValidUntil(java.time.LocalDateTime.now().plusYears(2));
        }

        resultRepository.save(result);
        System.out.println("✅ Certification generated: " + label);
    }

    private void generateRecommendations(Evaluation evaluation) {
        recommendationRepository.deleteByEvaluation_EvaluationId(evaluation.getEvaluationId());
        List<EvaluationResponse> responses = responseRepository.findByEvaluation_EvaluationId(evaluation.getEvaluationId());

        for (EvaluationResponse response : responses) {
            Integer level = response.getMaturityLevel() != null ? response.getMaturityLevel() : 0;
            if (level < 3) {
                Recommendation rec = new Recommendation();
                rec.setEvaluation(evaluation);
                rec.setPrincipleId(response.getPrincipleId());
                rec.setPracticeId(response.getPracticeId());
                rec.setCriterionId(response.getCriterionId());
                rec.setCurrentMaturityLevel(level);
                rec.setTargetMaturityLevel(Math.min(level + 1, 3));
                rec.setRecommendationText("Improve this area");
                rec.setPriority(level == 0 ? Recommendation.Priority.CRITICAL : Recommendation.Priority.HIGH);
                recommendationRepository.save(rec);
            }
        }
    }
}