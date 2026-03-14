package com.governance.evaluation.dto.evaluation;
import com.governance.evaluation.entity.CertificationLabel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EvaluationResultResponse {
	 private Long resultId;
	    private Double finalScore;
	    private Integer ranking;
	    private CertificationLabel certificationLabel;
	    private Boolean isCertified;
	    private LocalDate issuedDate;
	    private LocalDate validUntil;

}
