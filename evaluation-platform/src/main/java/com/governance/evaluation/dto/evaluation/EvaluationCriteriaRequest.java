package com.governance.evaluation.dto.evaluation;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

public class EvaluationCriteriaRequest {
	@NotNull(message = "Criteria ID is required")
    private Long criteriaId;
    
    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", message = "Score must be at least 0")
    @DecimalMax(value = "3.0", message = "Score must be at most 3")
    private Double score;
    
    @Min(value = 0, message = "Maturity level must be between 0 and 3")
    @Max(value = 3, message = "Maturity level must be between 0 and 3")
    private Integer maturityLevel;
    
    private String evidence;
    
    private String comment;
    
    private String fileUrl;

}
