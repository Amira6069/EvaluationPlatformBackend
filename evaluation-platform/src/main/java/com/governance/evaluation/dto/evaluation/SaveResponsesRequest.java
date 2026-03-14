package com.governance.evaluation.dto.evaluation;

import lombok.Data;
import java.util.List;

@Data
public class SaveResponsesRequest {
    private List<EvaluationResponseDTO> responses;
}