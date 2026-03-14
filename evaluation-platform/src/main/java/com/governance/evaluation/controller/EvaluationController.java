package com.governance.evaluation.controller;

import com.governance.evaluation.entity.*;
import com.governance.evaluation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    private final EvaluationResponseRepository responseRepository;
    private final EvaluationResultRepository resultRepository;
    private final RecommendationRepository recommendationRepository;

    /**
     * Get all evaluations for current user (organization)
     * GET /api/evaluations/my
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyEvaluations() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Evaluation> evaluations = evaluationRepository.findByOrganization_UserId(currentUser.getUserId());
            
            System.out.println("📋 Found " + evaluations.size() + " evaluations for user: " + currentUserEmail);

            return ResponseEntity.ok(evaluations);

        } catch (Exception e) {
            System.err.println("❌ Error fetching evaluations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch evaluations"));
        }
    }

    /**
     * Get evaluation by ID
     * GET /api/evaluations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEvaluationById(@PathVariable Long id) {
        try {
            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ FIXED: Allow evaluators and admins to view ANY evaluation
            boolean isOwner = evaluation.getOrganization().getUserId().equals(currentUser.getUserId());
            boolean isEvaluatorOrAdmin = currentUser.getRole().equals(UserRole.EVALUATOR) || currentUser.getRole().equals(UserRole.ADMIN);
            
            if (!isOwner && !isEvaluatorOrAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to view this evaluation"));
            }

            return ResponseEntity.ok(evaluation);

        } catch (Exception e) {
            System.err.println("❌ Error fetching evaluation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch evaluation"));
        }
    }

    /**
     * Create new evaluation
     * POST /api/evaluations
     */
    @PostMapping
    public ResponseEntity<?> createEvaluation(@RequestBody Map<String, String> request) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if user is an organization
            if (!"ORGANIZATION".equals(currentUser.getRole().toString())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only organizations can create evaluations"));
            }

            Evaluation evaluation = new Evaluation();
            evaluation.setOrganization(currentUser);
            evaluation.setName(request.get("name"));
            evaluation.setPeriod(request.get("period"));
            evaluation.setDescription(request.get("description"));
            evaluation.setStatus(EvaluationStatus.CREATED);
            evaluation.setCreatedAt(LocalDateTime.now());

            Evaluation savedEvaluation = evaluationRepository.save(evaluation);
            
            System.out.println("✅ Evaluation created: " + savedEvaluation.getName() + "  (ID: " + savedEvaluation.getEvaluationId() + ")");

            Map<String, Object> response = new HashMap<>();
            response.put("evaluationId", savedEvaluation.getEvaluationId());
            response.put("name", savedEvaluation.getName());
            response.put("period", savedEvaluation.getPeriod());
            response.put("description", savedEvaluation.getDescription());
            response.put("status", savedEvaluation.getStatus().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error creating evaluation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create evaluation", "details", e.getMessage()));
        }
    }

    /**
     * Update evaluation
     * PUT /api/evaluations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvaluation(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!evaluation.getOrganization().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to update this evaluation"));
            }

            if (request.containsKey("name")) evaluation.setName(request.get("name"));
            if (request.containsKey("period")) evaluation.setPeriod(request.get("period"));
            if (request.containsKey("description")) evaluation.setDescription(request.get("description"));

            evaluationRepository.save(evaluation);

            return ResponseEntity.ok(Map.of("message", "Evaluation updated successfully"));

        } catch (Exception e) {
            System.err.println("❌ Error updating evaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update evaluation"));
        }
    }

    /**
     * Save evaluation responses
     * POST /api/evaluations/{id}/responses
     */
    @PostMapping("/{id}/responses")
    public ResponseEntity<?> saveResponses(@PathVariable Long id, @RequestBody List<Map<String, Object>> responses) {
        try {
            System.out.println("📝 Saving " + responses.size() + " responses for evaluation: " + id);
            
            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));
            
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!evaluation.getOrganization().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to modify this evaluation"));
            }
            
            // Delete existing responses
            System.out.println("🗑️ Deleting existing responses...");
            responseRepository.deleteByEvaluation_EvaluationId(id);
            
            // Save new responses
            int savedCount = 0;
            for (Map<String, Object> respData : responses) {
                try {
                    EvaluationResponse response = new EvaluationResponse();
                    response.setEvaluation(evaluation);
                    
                    // Safer null checks
                    if (respData.containsKey("principleId") && respData.get("principleId") != null) {
                        response.setPrincipleId(((Number) respData.get("principleId")).intValue());
                    }
                    if (respData.containsKey("practiceId") && respData.get("practiceId") != null) {
                        response.setPracticeId(((Number) respData.get("practiceId")).intValue());
                    }
                    if (respData.containsKey("criterionId") && respData.get("criterionId") != null) {
                        response.setCriterionId(((Number) respData.get("criterionId")).longValue());
                    }
                    if (respData.containsKey("maturityLevel") && respData.get("maturityLevel") != null) {
                        response.setMaturityLevel(((Number) respData.get("maturityLevel")).intValue());
                    } else {
                        response.setMaturityLevel(0); // Default to 0 if missing
                    }
                    if (respData.containsKey("evidence") && respData.get("evidence") != null) {
                        response.setEvidence((String) respData.get("evidence"));
                    }
                    if (respData.containsKey("comments") && respData.get("comments") != null) {
                        response.setComments((String) respData.get("comments"));
                    }
                    
                    responseRepository.save(response);
                    savedCount++;
                    
                } catch (Exception e) {
                    System.err.println("❌ Error saving response: " + e.getMessage());
                    System.err.println("   Response data: " + respData);
                    e.printStackTrace();
                    // Continue with next response instead of failing entire batch
                }
            }
            
            System.out.println("✅ Saved " + savedCount + "/" + responses.size() + " responses successfully");
            
            // Update evaluation status
            evaluation.setStatus(EvaluationStatus.IN_PROGRESS);
            evaluationRepository.save(evaluation);
            
            return ResponseEntity.ok(Map.of(
                "message", "Responses saved successfully",
                "saved", savedCount,
                "total", responses.size()
            ));
            
        } catch (Exception e) {
            System.err.println("❌ Error in saveResponses endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to save responses", 
                        "details", e.getMessage(),
                        "class", e.getClass().getName()
                    ));
        }
    }

    /**
     * Submit evaluation
     * POST /api/evaluations/{id}/submit
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitEvaluation(@PathVariable Long id) {
        try {
            System.out.println("📤 Submitting evaluation: " + id);
            
            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!evaluation.getOrganization().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to submit this evaluation"));
            }

            // Calculate score
            List<EvaluationResponse> responses = responseRepository.findByEvaluation_EvaluationId(id);
            
            System.out.println("📊 Found " + responses.size() + " responses for evaluation");
            
            if (responses.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot submit evaluation without responses"));
            }

            double totalScore = 0;
            int maxScore = responses.size() * 3; // Max maturity level is 3
            
            for (EvaluationResponse response : responses) {
                if (response.getMaturityLevel() != null) {
                    totalScore += response.getMaturityLevel();
                }
            }

            double percentageScore = (totalScore / maxScore) * 100;

            evaluation.setStatus(EvaluationStatus.SUBMITTED);
            evaluation.setTotalScore(percentageScore);
            evaluation.setSubmittedAt(LocalDateTime.now());
            evaluationRepository.save(evaluation);

            System.out.println("✅ Evaluation submitted. Score: " + Math.round(percentageScore) + "%");

            Map<String, Object> result = new HashMap<>();
            result.put("evaluationId", evaluation.getEvaluationId());
            result.put("totalScore", percentageScore);
            result.put("status", "SUBMITTED");
            result.put("message", "Evaluation submitted successfully");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("❌ Error submitting evaluation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to submit evaluation", 
                        "details", e.getMessage()
                    ));
        }
    }

    /**
     * Delete evaluation
     * DELETE /api/evaluations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvaluation(@PathVariable Long id) {
        try {
            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!evaluation.getOrganization().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to delete this evaluation"));
            }

            evaluationRepository.delete(evaluation);

            System.out.println("🗑️ Evaluation deleted: " + id);

            return ResponseEntity.ok(Map.of("message", "Evaluation deleted successfully"));

        } catch (Exception e) {
            System.err.println("❌ Error deleting evaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete evaluation"));
        }
    }

    /**
     * Get evaluation result
     * GET /api/evaluations/{id}/result
     */
    @GetMapping("/{id}/result")
    public ResponseEntity<?> getEvaluationResult(@PathVariable Long id) {
        try {
            Optional<EvaluationResult> result = resultRepository.findByEvaluation_EvaluationId(id);

            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No result found for this evaluation"));
            }

            return ResponseEntity.ok(result.get());

        } catch (Exception e) {
            System.err.println("❌ Error fetching result: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch result"));
        }
    }

    /**
     * Get recommendations for evaluation
     * GET /api/evaluations/{id}/recommendations
     */
    @GetMapping("/{id}/recommendations")
    public ResponseEntity<?> getRecommendations(@PathVariable Long id) {
        try {
            List<Recommendation> recommendations = recommendationRepository.findByEvaluation_EvaluationId(id);

            return ResponseEntity.ok(recommendations);

        } catch (Exception e) {
            System.err.println("❌ Error fetching recommendations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch recommendations"));
        }
    }
}