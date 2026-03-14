package com.governance.evaluation.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "good_practices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodPractice {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "practice_id")
	    private Long practiceId;
	    
	    @Column(nullable = false, columnDefinition = "TEXT")
	    private String description;
	    
	    @Column(length = 100)
	    private String reference;  // NEW FIELD
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "principle_id", nullable = false)
	    private Principle principle;
	    
	    @OneToMany(mappedBy = "goodPractice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    private List<Criteria> criteria;
	

}
