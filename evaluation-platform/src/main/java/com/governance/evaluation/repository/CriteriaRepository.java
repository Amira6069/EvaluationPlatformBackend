package com.governance.evaluation.repository;
import com.governance.evaluation.entity.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria, Long>{
	 List<Criteria> findByGoodPracticePracticeId(Long practiceId);
	    
	    @Query("SELECT c FROM Criteria c JOIN c.goodPractice gp JOIN gp.principle p WHERE p.principleId = :principleId")
	    List<Criteria> findByPrincipleId(Long principleId);

}
