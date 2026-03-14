package com.governance.evaluation.repository;
import com.governance.evaluation.entity.Proof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProofRepository extends JpaRepository<Proof, Long>{
	  List<Proof> findByCriteriaCriteriaId(Long criteriaId);

}
