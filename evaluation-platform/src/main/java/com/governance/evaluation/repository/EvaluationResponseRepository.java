package com.governance.evaluation.repository;

import com.governance.evaluation.entity.EvaluationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationResponseRepository extends JpaRepository<EvaluationResponse, Long> {
    
    List<EvaluationResponse> findByEvaluation_EvaluationId(Long evaluationId);
    
    Optional<EvaluationResponse> findByEvaluation_EvaluationIdAndPrincipleIdAndPracticeIdAndCriterionId(
        Long evaluationId, Integer principleId, Integer practiceId, Integer criterionId
    );
    
    void deleteByEvaluation_EvaluationId(Long evaluationId);
}