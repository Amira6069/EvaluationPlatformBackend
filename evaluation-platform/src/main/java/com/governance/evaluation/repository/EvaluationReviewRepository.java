package com.governance.evaluation.repository;

import com.governance.evaluation.entity.EvaluationReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationReviewRepository extends JpaRepository<EvaluationReview, Long> {
    
    // Find review by evaluation and evaluator
    Optional<EvaluationReview> findByEvaluation_EvaluationIdAndEvaluator_UserId(Long evaluationId, Long evaluatorId);
    
    // Find all reviews for an evaluation
    List<EvaluationReview> findByEvaluation_EvaluationId(Long evaluationId);
    
    // Find all reviews by an evaluator
    List<EvaluationReview> findByEvaluator_UserId(Long evaluatorId);
    
    // Find pending reviews for an evaluator
    List<EvaluationReview> findByEvaluator_UserIdAndApprovalStatus(
        Long evaluatorId, 
        EvaluationReview.ApprovalStatus status
    );
}