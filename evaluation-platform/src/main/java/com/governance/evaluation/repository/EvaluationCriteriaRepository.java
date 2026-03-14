package com.governance.evaluation.repository;
import com.governance.evaluation.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface EvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Long>{
List<EvaluationCriteria> findByEvaluationEvaluationId(Long evaluationId);
    
    @Query("SELECT ec FROM EvaluationCriteria ec WHERE ec.evaluation.evaluationId = :evalId AND ec.criteria.criteriaId = :criteriaId")
    Optional<EvaluationCriteria> findByEvaluationAndCriteria(@Param("evalId") Long evalId, @Param("criteriaId") Long criteriaId);
    
    @Query("SELECT AVG(ec.score) FROM EvaluationCriteria ec WHERE ec.evaluation.evaluationId = :evalId")
    Double calculateAverageScore(@Param("evalId") Long evalId);
    
    void deleteByEvaluationEvaluationId(Long evaluationId);

}
