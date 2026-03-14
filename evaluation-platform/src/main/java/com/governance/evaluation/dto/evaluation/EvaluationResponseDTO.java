package com.governance.evaluation.dto.evaluation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponseDTO {
    private Long responseId;
    private Integer principleId;
    private Integer practiceId;
    private Integer criterionId;
    private Integer maturityLevel;
    private String evidence;
    private String comments;
}