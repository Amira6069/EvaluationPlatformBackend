package com.governance.evaluation.dto.evaluation;
import com.governance.evaluation.entity.EvaluationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EvaluationResponse {
	 private Long evaluationId;
	    private String name;
	    private String description;
	    private String period;
	    private EvaluationStatus status;
	    private Double totalScore;
	    private LocalDateTime submissionDate;
	    private LocalDateTime createdAt;
	    private LocalDateTime updatedAt;
	    private Long organizationId;
	    private String organizationName;
	    private List<EvaluationCriteriaResponse> criteriaResponses;
	    private EvaluationResultResponse result;

}
