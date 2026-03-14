package com.governance.evaluation.dto.evaluation;
import com.governance.evaluation.entity.EvaluationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EvaluationAdminDTO {
	 private Long evaluationId;
	    private String name;
	    private String description;
	    private String period;
	    private EvaluationStatus status;
	    private Double totalScore;
	    private LocalDateTime createdAt;
	    
	    // Organization info
	    private Long organizationId;
	    private String organizationName;
	    private String organizationEmail;

}
