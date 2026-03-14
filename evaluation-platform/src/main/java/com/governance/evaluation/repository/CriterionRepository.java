package com.governance.evaluation.repository;

import com.governance.evaluation.entity.Criterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CriterionRepository extends JpaRepository<Criterion, Long> {
    List<Criterion> findByPractice_PracticeIdAndIsActiveTrueOrderByOrderIndexAsc(Long practiceId);
    List<Criterion> findByPractice_PracticeIdOrderByOrderIndexAsc(Long practiceId);
}