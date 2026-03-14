package com.governance.evaluation.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "criteria")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Criteria {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "criteria_id")
	    private Long criteriaId;
	    
	    @Column(nullable = false, columnDefinition = "TEXT")
	    private String description;
	    
	    @Column(name = "maturity_level")
	    private Integer maturityLevel;  // NEW FIELD (0-3 scale)
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "practice_id", nullable = false)
	    private GoodPractice goodPractice;
	    
	    @OneToMany(mappedBy = "criteria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    private List<Proof> proofs;  
}
