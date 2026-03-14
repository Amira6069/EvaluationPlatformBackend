package com.governance.evaluation.dto.evaluation;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class EvaluationUpdateRequest {
	 @Size(max = 200)
	    private String name;
	    
	    @Size(max = 1000)
	    private String description;
	    
	    @Size(max = 50)
	    private String period;
}
