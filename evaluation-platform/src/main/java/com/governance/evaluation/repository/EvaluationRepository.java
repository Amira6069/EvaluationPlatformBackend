package com.governance.evaluation.repository;

import com.governance.evaluation.entity.Evaluation;
import com.governance.evaluation.entity.EvaluationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    
    // Find evaluations by organization ID
    List<Evaluation> findByOrganization_UserId(Long userId);
    
    // Find evaluations by status
    List<Evaluation> findByStatus(EvaluationStatus status);
    
    // Find evaluations by organization and status
    List<Evaluation> findByOrganization_UserIdAndStatus(Long userId, EvaluationStatus status);
}