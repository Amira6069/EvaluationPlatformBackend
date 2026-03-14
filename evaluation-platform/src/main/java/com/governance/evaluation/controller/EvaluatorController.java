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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evaluator")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EvaluatorController {

    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    private final EvaluationResultRepository resultRepository;
    private final RecommendationRepository recommendationRepository;
    private final EvaluationResponseRepository responseRepository;

    /**
     * Get evaluation queue (all SUBMITTED evaluations)
     * GET /api/evaluator/queue
     */
    /**
     * Get evaluation queue (all SUBMITTED evaluations)
     * GET /api/evaluator/queue
     */
    @GetMapping("/queue")
    public ResponseEntity<?> getEvaluationQueue() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!currentUser.getRole().equals(UserRole.EVALUATOR) && !currentUser.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only evaluators can access the queue"));
            }

            List<Evaluation> submittedEvaluations = evaluationRepository.findByStatus(EvaluationStatus.SUBMITTED);

            System.out.println("📋 Found " + submittedEvaluations.size() + " evaluations in queue");

            // ✅ BUILD RESPONSE WITH ORGANIZATION DATA
            List<Map<String, Object>> response = submittedEvaluations.stream()
                .map(evaluation -> {
                    Map<String, Object> evalMap = new HashMap<>();
                    evalMap.put("evaluationId", evaluation.getEvaluationId());
                    evalMap.put("name", evaluation.getName());
                    evalMap.put("period", evaluation.getPeriod());
                    evalMap.put("description", evaluation.getDescription());
                    evalMap.put("status", evaluation.getStatus().toString());
                    evalMap.put("totalScore", evaluation.getTotalScore());
                    evalMap.put("createdAt", evaluation.getCreatedAt());
                    evalMap.put("submittedAt", evaluation.getSubmittedAt());
                    
                    // ✅ ADD ORGANIZATION INFO
                    Map<String, Object> orgMap = new HashMap<>();
                    orgMap.put("userId", evaluation.getOrganization().getUserId());
                    orgMap.put("name", evaluation.getOrganization().getName());
                    orgMap.put("email", evaluation.getOrganization().getEmail());
                    evalMap.put("organization", orgMap);
                    
                    return evalMap;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error fetching queue: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch evaluation queue"));
        }
    }

    /**
     * Approve evaluation
     * POST /api/evaluator/evaluations/{id}/approve
     */
    @PostMapping("/evaluations/{id}/approve")
    public ResponseEntity<?> approveEvaluation(@PathVariable Long id, @RequestBody(required = false) Map<String, String> request) {
        try {
            System.out.println("✅ Approving evaluation: " + id);

            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!currentUser.getRole().equals(UserRole.EVALUATOR) && !currentUser.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only evaluators can approve evaluations"));
            }

            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            if (!evaluation.getStatus().equals(EvaluationStatus.SUBMITTED)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Only submitted evaluations can be approved"));
            }

            // Update evaluation status
            evaluation.setStatus(EvaluationStatus.APPROVED);
            evaluation.setApprovedAt(LocalDateTime.now());
            evaluationRepository.save(evaluation);

            // Generate evaluation result
            EvaluationResult result = generateEvaluationResult(evaluation);
            resultRepository.save(result);

            // Generate recommendations
            List<Recommendation> recommendations = generateRecommendations(evaluation);
            recommendationRepository.saveAll(recommendations);

            System.out.println("✅ Evaluation approved with " + recommendations.size() + " recommendations");

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Evaluation approved successfully");
            response.put("evaluationId", evaluation.getEvaluationId());
            response.put("score", evaluation.getTotalScore());
            response.put("certificationLevel", result.getCertificationLevel().toString());
            response.put("recommendationsCount", recommendations.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error approving evaluation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to approve evaluation", "details", e.getMessage()));
        }
    }

    /**
     * Reject evaluation
     * POST /api/evaluator/evaluations/{id}/reject
     */
    @PostMapping("/evaluations/{id}/reject")
    public ResponseEntity<?> rejectEvaluation(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            System.out.println("❌ Rejecting evaluation: " + id);

            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!currentUser.getRole().equals(UserRole.EVALUATOR) && !currentUser.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only evaluators can reject evaluations"));
            }

            Evaluation evaluation = evaluationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            if (!evaluation.getStatus().equals(EvaluationStatus.SUBMITTED)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Only submitted evaluations can be rejected"));
            }

            String reason = request != null ? request.get("reason") : "Not specified";

            evaluation.setStatus(EvaluationStatus.REJECTED);
            evaluationRepository.save(evaluation);

            System.out.println("❌ Evaluation rejected. Reason: " + reason);

            return ResponseEntity.ok(Map.of(
                "message", "Evaluation rejected",
                "evaluationId", evaluation.getEvaluationId(),
                "reason", reason
            ));

        } catch (Exception e) {
            System.err.println("❌ Error rejecting evaluation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reject evaluation"));
        }
    }

    /**
     * Generate evaluation result
     */
    private EvaluationResult generateEvaluationResult(Evaluation evaluation) {
        EvaluationResult result = new EvaluationResult();
        result.setEvaluation(evaluation);
        result.setTotalScore(evaluation.getTotalScore());

        // Determine certification level
        double score = evaluation.getTotalScore();
        if (score >= 90) {
            result.setCertificationLevel(CertificationLevel.PLATINUM);
        } else if (score >= 80) {
            result.setCertificationLevel(CertificationLevel.GOLD);
        } else if (score >= 65) {
            result.setCertificationLevel(CertificationLevel.SILVER);
        } else if (score >= 50) {
            result.setCertificationLevel(CertificationLevel.BRONZE);
        } else {
            result.setCertificationLevel(CertificationLevel.NOT_CERTIFIED);
        }

        result.setIssuedDate(LocalDateTime.now());
        result.setExpiryDate(LocalDateTime.now().plusYears(1)); // Valid for 1 year

        return result;
    }

    /**
     * Generate AI recommendations based on responses
     */
    private List<Recommendation> generateRecommendations(Evaluation evaluation) {
        List<Recommendation> recommendations = new ArrayList<>();

        // Get all responses for this evaluation
        List<EvaluationResponse> responses = responseRepository.findByEvaluation_EvaluationId(evaluation.getEvaluationId());

        for (EvaluationResponse response : responses) {
            // Only generate recommendations for criteria with maturity level < 3
            if (response.getMaturityLevel() != null && response.getMaturityLevel() < 3) {
                Recommendation rec = new Recommendation();
                rec.setEvaluation(evaluation);
                rec.setPrincipleId(response.getPrincipleId());
                rec.setPracticeId(response.getPracticeId());
                rec.setCriterionId(response.getCriterionId());
                rec.setCurrentMaturityLevel(response.getMaturityLevel());
                rec.setTargetMaturityLevel(3); // Target is always full implementation

                // Determine priority
                if (response.getMaturityLevel() == 0) {
                    rec.setPriority(RecommendationPriority.CRITICAL);
                } else if (response.getMaturityLevel() == 1) {
                    rec.setPriority(RecommendationPriority.HIGH);
                } else {
                    rec.setPriority(RecommendationPriority.MEDIUM);
                }

                // Generate recommendation text
                rec.setRecommendation(generateRecommendationText(response));
                rec.setActionPlan(generateActionPlan(response));

                recommendations.add(rec);
            }
        }

        System.out.println("📝 Generated " + recommendations.size() + " recommendations");

        return recommendations;
    }

    private String generateRecommendationText(EvaluationResponse response) {
        int current = response.getMaturityLevel();
        return "Improve from level " + current + " to level 3 (Fully Implemented). "
                + "Current status indicates " + getLevelDescription(current) + ". "
                + "Focus on establishing formal processes and documentation.";
    }

    private String generateActionPlan(EvaluationResponse response) {
        int current = response.getMaturityLevel();
        if (current == 0) {
            return "1. Establish basic framework\n2. Document initial procedures\n3. Assign responsibilities\n4. Implement monitoring";
        } else if (current == 1) {
            return "1. Formalize existing processes\n2. Enhance documentation\n3. Implement regular reviews\n4. Establish metrics";
        } else {
            return "1. Complete remaining gaps\n2. Enhance monitoring\n3. Implement continuous improvement\n4. Document best practices";
        }
    }

    private String getLevelDescription(int level) {
        switch (level) {
            case 0: return "no implementation";
            case 1: return "partial implementation";
            case 2: return "substantial implementation with some gaps";
            default: return "unknown level";
        }
    }
}