package com.governance.evaluation.dto.evaluation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EvaluationCreateRequest {
	@NotBlank(message = "Evaluation name is required")
    @Size(max = 200)
    private String name;
    
    @Size(max = 1000)
    private String description;
    
    @NotBlank(message = "Period is required")
    @Size(max = 50)
    private String period;
    
    @NotNull(message = "Organization ID is required")
    private Long organizationId;
}

