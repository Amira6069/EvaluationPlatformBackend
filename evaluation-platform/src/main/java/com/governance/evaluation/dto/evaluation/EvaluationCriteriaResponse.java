package com.governance.evaluation.dto.evaluation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EvaluationCriteriaResponse {
	 private Long id;
	    private Long criteriaId;
	    private String criteriaDescription;
	    private Double score;
	    private Integer maturityLevel;
	    private String evidence;
	    private String comment;
	    private String fileUrl;

}
