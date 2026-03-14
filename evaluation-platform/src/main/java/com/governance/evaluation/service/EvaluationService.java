package com.governance.evaluation.service;

import com.governance.evaluation.entity.*;
import com.governance.evaluation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    
    private final EvaluationRepository evaluationRepository;
    private final EvaluationResponseRepository responseRepository;
    private final EvaluationResultRepository resultRepository;
    private final RecommendationRepository recommendationRepository;
    
    @Transactional(readOnly = true)
    public Optional<Evaluation> findById(Long id) {
        return evaluationRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Evaluation> findAll() {
        return evaluationRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Evaluation> findByOrganizationId(Long organizationId) {
        return evaluationRepository.findByOrganization_UserId(organizationId);
    }
    
    @Transactional(readOnly = true)
    public List<Evaluation> findByStatus(EvaluationStatus status) {
        return evaluationRepository.findByStatus(status);
    }
    
    @Transactional
    public Evaluation save(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }
    
    @Transactional
    public Evaluation create(Evaluation evaluation) {
        evaluation.setStatus(EvaluationStatus.CREATED);
        evaluation.setTotalScore(0.0);
        evaluation.setCreatedAt(LocalDateTime.now());
        return evaluationRepository.save(evaluation);
    }
    
    @Transactional
    public Evaluation update(Evaluation evaluation) {
        evaluation.setUpdatedAt(LocalDateTime.now());
        return evaluationRepository.save(evaluation);
    }
    
    @Transactional
    public Evaluation submit(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        
        if (evaluation.getStatus() != EvaluationStatus.CREATED && 
            evaluation.getStatus() != EvaluationStatus.IN_PROGRESS) {
            throw new IllegalStateException("Evaluation already submitted");
        }
        
        // Calculate total score
        Double totalScore = calculateTotalScore(evaluationId);
        
        evaluation.setStatus(EvaluationStatus.SUBMITTED);
        evaluation.setSubmissionDate(LocalDateTime.now());
        evaluation.setTotalScore(totalScore);
        
        return evaluationRepository.save(evaluation);
    }
    
    @Transactional
    public Evaluation approve(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        
        evaluation.setStatus(EvaluationStatus.APPROVED);
        Evaluation approved = evaluationRepository.save(evaluation);
        
        // Generate result and recommendations
        generateEvaluationResult(evaluation);
        generateRecommendations(evaluation);
        
        return approved;
    }
    
    @Transactional
    public Evaluation reject(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        
        evaluation.setStatus(EvaluationStatus.REJECTED);
        return evaluationRepository.save(evaluation);
    }
    
    @Transactional
    public void delete(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        
        if (evaluation.getStatus() != EvaluationStatus.CREATED) {
            throw new IllegalStateException("Cannot delete submitted evaluation");
        }
        
        evaluationRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Double calculateTotalScore(Long evaluationId) {
        List<EvaluationResponse> responses = responseRepository.findByEvaluation_EvaluationId(evaluationId);
        
        if (responses.isEmpty()) {
            return 0.0;
        }
        
        double sum = responses.stream()
                .mapToDouble(r -> r.getMaturityLevel() != null ? r.getMaturityLevel() : 0.0)
                .sum();
        
        double maxPossibleScore = responses.size() * 3.0; // Max maturity level is 3
        return (sum / maxPossibleScore) * 100.0; // Percentage
    }
    
    @Transactional
    public void generateEvaluationResult(Evaluation evaluation) {
        Optional<EvaluationResult> existing = resultRepository.findByEvaluation_EvaluationId(evaluation.getEvaluationId());
        if (existing.isPresent()) {
            return;
        }
        
        Double score = evaluation.getTotalScore() != null ? evaluation.getTotalScore() : 0.0;
        
        String certificationLabel;
        boolean isCertified;
        
        if (score >= 90) {
            certificationLabel = "CERTIFIED_PLATINUM";
            isCertified = true;
        } else if (score >= 80) {
            certificationLabel = "CERTIFIED_GOLD";
            isCertified = true;
        } else if (score >= 65) {
            certificationLabel = "CERTIFIED_SILVER";
            isCertified = true;
        } else if (score >= 50) {
            certificationLabel = "CERTIFIED_BRONZE";
            isCertified = true;
        } else {
            certificationLabel = "NOT_CERTIFIED";
            isCertified = false;
        }
        
        EvaluationResult result = new EvaluationResult();
        result.setEvaluation(evaluation);
        result.setFinalScore(score);
        result.setCertificationLabel(certificationLabel);
        result.setIsCertified(isCertified);
        
        if (isCertified) {
            result.setValidUntil(LocalDateTime.now().plusYears(2));
        }
        
        resultRepository.save(result);
    }
    
    @Transactional
    public void generateRecommendations(Evaluation evaluation) {
        // Delete existing recommendations
        recommendationRepository.deleteByEvaluation_EvaluationId(evaluation.getEvaluationId());
        
        List<EvaluationResponse> responses = responseRepository.findByEvaluation_EvaluationId(evaluation.getEvaluationId());
        
        for (EvaluationResponse response : responses) {
            Integer maturityLevel = response.getMaturityLevel() != null ? response.getMaturityLevel() : 0;
            
            if (maturityLevel < 3) {
                Recommendation recommendation = new Recommendation();
                recommendation.setEvaluation(evaluation);
                recommendation.setPrincipleId(response.getPrincipleId());
                recommendation.setPracticeId(response.getPracticeId());
                recommendation.setCriterionId(response.getCriterionId());
                recommendation.setCurrentMaturityLevel(maturityLevel);
                recommendation.setTargetMaturityLevel(Math.min(maturityLevel + 1, 3));
                recommendation.setRecommendationText(generateRecommendationText(maturityLevel));
                
                Recommendation.Priority priority;
                if (maturityLevel == 0) {
                    priority = Recommendation.Priority.CRITICAL;
                } else if (maturityLevel == 1) {
                    priority = Recommendation.Priority.HIGH;
                } else {
                    priority = Recommendation.Priority.MEDIUM;
                }
                recommendation.setPriority(priority);
                
                recommendationRepository.save(recommendation);
            }
        }
    }
    
    private String generateRecommendationText(Integer currentLevel) {
        switch (currentLevel) {
            case 0:
                return "CRITICAL: Establish foundational framework. Document current processes, identify stakeholders, and create implementation plan.";
            case 1:
                return "HIGH PRIORITY: Accelerate development. Complete implementation, conduct training, and establish monitoring mechanisms.";
            case 2:
                return "OPTIMIZATION: Enhance and validate. Conduct audits, gather feedback, implement continuous improvement cycles.";
            default:
                return "Maintain current excellence level.";
        }
    }
}
