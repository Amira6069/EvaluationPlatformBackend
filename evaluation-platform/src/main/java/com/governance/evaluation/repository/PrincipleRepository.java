package com.governance.evaluation.repository;

import com.governance.evaluation.entity.Principle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrincipleRepository extends JpaRepository<Principle, Long> {
    List<Principle> findByIsActiveTrueOrderByOrderIndexAsc();
    List<Principle> findAllByOrderByOrderIndexAsc();
}