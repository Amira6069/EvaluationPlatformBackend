package com.governance.evaluation.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluation_criteria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCriteria {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "evaluation_id", nullable = false)
	    private Evaluation evaluation;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "criteria_id", nullable = false)
	    private Criteria criteria;
	    
	    @Column(nullable = false)
	    private Double score;
	    
	    @Column(columnDefinition = "TEXT")
	    private String evidence;
	    
	    @Column(columnDefinition = "TEXT")
	    private String comment;
	    
	    @Column(name = "file_url", length = 500)
	    private String fileUrl;
	    
	    @Column(name = "maturity_level")
	    private Integer maturityLevel;
}
