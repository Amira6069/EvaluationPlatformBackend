package com.governance.evaluation.repository;

import com.governance.evaluation.entity.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Long> {
    
    // Find result by evaluation ID
    Optional<EvaluationResult> findByEvaluation_EvaluationId(Long evaluationId);
    
    // Check if result exists for evaluation
    boolean existsByEvaluation_EvaluationId(Long evaluationId);
}