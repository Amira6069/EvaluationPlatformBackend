package com.governance.evaluation.repository;

import com.governance.evaluation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    
    // Find all recommendations for an evaluation
    List<Recommendation> findByEvaluation_EvaluationId(Long evaluationId);
    
    // Find recommendations by priority
    List<Recommendation> findByEvaluation_EvaluationIdAndPriority(
        Long evaluationId, 
        Recommendation.Priority priority
    );
    
    // Delete all recommendations for an evaluation
    void deleteByEvaluation_EvaluationId(Long evaluationId);
}