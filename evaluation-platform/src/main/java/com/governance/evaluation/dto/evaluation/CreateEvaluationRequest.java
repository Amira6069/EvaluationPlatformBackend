package com.governance.evaluation.dto.evaluation;

import lombok.Data;

@Data
public class CreateEvaluationRequest {
    private String name;
    private String description;
    private String period;
}