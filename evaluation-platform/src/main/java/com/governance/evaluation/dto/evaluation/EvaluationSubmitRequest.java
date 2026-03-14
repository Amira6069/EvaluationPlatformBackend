package com.governance.evaluation.dto.evaluation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data

public class EvaluationSubmitRequest {
	  @NotEmpty(message = "At least one criteria response is required")
	    @Valid
	    private List<EvaluationCriteriaRequest> criteriaResponses;

}
