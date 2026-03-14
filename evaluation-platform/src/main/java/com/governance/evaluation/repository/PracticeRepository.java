package com.governance.evaluation.repository;

import com.governance.evaluation.entity.Practice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PracticeRepository extends JpaRepository<Practice, Long> {
    List<Practice> findByPrinciple_PrincipleIdAndIsActiveTrueOrderByOrderIndexAsc(Long principleId);
    List<Practice> findByPrinciple_PrincipleIdOrderByOrderIndexAsc(Long principleId);
}
