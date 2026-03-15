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
    private final PrincipleRepository principleRepository;
    private final PracticeRepository practiceRepository;
    private final CriterionRepository criterionRepository;

    /**
     * Get my evaluations
     * GET /api/evaluations/my
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyEvaluations() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Evaluation> evaluations = evaluationRepository.findByOrganization_UserId(currentUser.getUserId());
            
            System.out.println("📋 Found " + evaluations.size() + " evaluations for user: " + currentUser.getEmail());

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

            // ✅ CRITICAL: Use .toString() for comparison
            String userRole = currentUser.getRole().toString();
            boolean isOwner = evaluation.getOrganization().getUserId().equals(currentUser.getUserId());
            boolean isEvaluator = "EVALUATOR".equals(userRole);
            boolean isAdmin = "ADMIN".equals(userRole);
            
            System.out.println("🔐 Authorization check:");
            System.out.println("  User: " + currentUser.getEmail());
            System.out.println("  Role: " + userRole);
            System.out.println("  Is Owner: " + isOwner);
            System.out.println("  Is Evaluator: " + isEvaluator);
            System.out.println("  Is Admin: " + isAdmin);
            
            if (!isOwner && !isEvaluator && !isAdmin) {
                System.err.println("❌ Access DENIED for user: " + currentUser.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to view this evaluation"));
            }

            System.out.println("✅ Access GRANTED - User " + currentUser.getEmail() + " accessing evaluation " + id);

            return ResponseEntity.ok(evaluation);

        } catch (Exception e) {
            System.err.println("❌ Error fetching evaluation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch evaluation"));
        }
    }

    /**
     * Get evaluation responses
     * GET /api/evaluations/{id}/responses
     */
    @GetMapping("/{id}/responses")
    public ResponseEntity<?> getEvaluationResponses(@PathVariable Long id) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            // ✅ CRITICAL: Use .toString() for comparison
            String userRole = currentUser.getRole().toString();
            boolean isOwner = evaluation.getOrganization().getUserId().equals(currentUser.getUserId());
            boolean isEvaluator = "EVALUATOR".equals(userRole);
            boolean isAdmin = "ADMIN".equals(userRole);
            
            System.out.println("🔐 Responses authorization check:");
            System.out.println("  User: " + currentUser.getEmail());
            System.out.println("  Role: " + userRole);
            System.out.println("  Is Owner: " + isOwner);
            System.out.println("  Is Evaluator: " + isEvaluator);
            System.out.println("  Is Admin: " + isAdmin);
            
            if (!isOwner && !isEvaluator && !isAdmin) {
                System.err.println("❌ Access DENIED for responses");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to view responses"));
            }

            List<EvaluationResponse> responses = responseRepository.findByEvaluation_EvaluationId(id);
            
            System.out.println("✅ Access GRANTED - Retrieved " + responses.size() + " responses for evaluation " + id);

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            System.err.println("❌ Error fetching responses: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch responses"));
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

            Evaluation evaluation = new Evaluation();
            evaluation.setName(request.get("name"));
            evaluation.setPeriod(request.get("period"));
            evaluation.setDescription(request.get("description"));
            evaluation.setOrganization(currentUser);
            evaluation.setStatus(EvaluationStatus.CREATED);
            evaluation.setTotalScore(0.0);
            evaluation.setCreatedAt(LocalDateTime.now());

            Evaluation savedEvaluation = evaluationRepository.save(evaluation);
            
            System.out.println("✅ Evaluation created: " + savedEvaluation.getName() + " (ID: " + savedEvaluation.getEvaluationId() + ")");

            return ResponseEntity.ok(savedEvaluation);

        } catch (Exception e) {
            System.err.println("❌ Error creating evaluation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create evaluation"));
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

            Evaluation updatedEvaluation = evaluationRepository.save(evaluation);
            
            System.out.println("✅ Evaluation updated: " + updatedEvaluation.getEvaluationId());

            return ResponseEntity.ok(updatedEvaluation);

        } catch (Exception e) {
            System.err.println("❌ Error updating evaluation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update evaluation"));
        }
    }

    /**
     * Save responses
     * POST /api/evaluations/{id}/responses
     */
    @PostMapping("/{id}/responses")
    public ResponseEntity<?> saveResponses(@PathVariable Long id, @RequestBody List<Map<String, Object>> responsesData) {
        try {
            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!evaluation.getOrganization().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to save responses for this evaluation"));
            }

            responseRepository.deleteByEvaluation_EvaluationId(id);

            for (Map<String, Object> responseData : responsesData) {
                EvaluationResponse response = new EvaluationResponse();
                response.setEvaluation(evaluation);
                response.setPrincipleId(((Number) responseData.get("principleId")).longValue());
                response.setPracticeId(((Number) responseData.get("practiceId")).longValue());
                response.setCriterionId(((Number) responseData.get("criterionId")).longValue());
                response.setMaturityLevel((Integer) responseData.get("maturityLevel"));
                response.setEvidence((String) responseData.get("evidence"));
                response.setComments((String) responseData.get("comments"));
                response.setEvidenceFile((String) responseData.get("evidenceFile")); // ✅ NEW
                response.setCreatedAt(LocalDateTime.now());

                responseRepository.save(response);
            }

            evaluation.setStatus(EvaluationStatus.IN_PROGRESS);
            evaluationRepository.save(evaluation);

            System.out.println("✅ Saved " + responsesData.size() + " responses for evaluation " + id);

            return ResponseEntity.ok(Map.of(
                "message", "Responses saved successfully",
                "count", responsesData.size()
            ));

        } catch (Exception e) {
            System.err.println("❌ Error saving responses: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save responses"));
        }
    }

    /**
     * Submit evaluation
     * POST /api/evaluations/{id}/submit
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitEvaluation(@PathVariable Long id) {
        try {
            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!evaluation.getOrganization().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to submit this evaluation"));
            }

            List<EvaluationResponse> responses = responseRepository.findByEvaluation_EvaluationId(id);

            if (responses.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot submit evaluation without responses"));
            }

            double totalScore = 0;
            int totalCriteria = responses.size();

            for (EvaluationResponse response : responses) {
                totalScore += (response.getMaturityLevel() / 3.0) * 100;
            }

            double averageScore = totalScore / totalCriteria;

            evaluation.setTotalScore(averageScore);
            evaluation.setStatus(EvaluationStatus.SUBMITTED);
            evaluation.setSubmittedAt(LocalDateTime.now());
            
            Evaluation submittedEvaluation = evaluationRepository.save(evaluation);

            System.out.println("✅ Evaluation submitted: " + id + " with score: " + averageScore + "%");

            return ResponseEntity.ok(submittedEvaluation);

        } catch (Exception e) {
            System.err.println("❌ Error submitting evaluation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit evaluation"));
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
            
            System.out.println("✅ Evaluation deleted: " + id);

            return ResponseEntity.ok(Map.of("message", "Evaluation deleted successfully"));

        } catch (Exception e) {
            System.err.println("❌ Error deleting evaluation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete evaluation"));
        }
    }
}