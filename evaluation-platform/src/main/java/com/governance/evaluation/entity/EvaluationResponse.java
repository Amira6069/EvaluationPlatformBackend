package com.governance.evaluation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_responses")
@Data
public class EvaluationResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @ManyToOne
    @JoinColumn(name = "evaluation_id", nullable = false)
    @JsonIgnore
    private Evaluation evaluation;

    @Column(name = "principle_id")
    private Long principleId;

    @Column(name = "practice_id")
    private Long practiceId;

    @Column(name = "criterion_id")
    private Long criterionId;

    @Column(name = "maturity_level")
    private Integer maturityLevel;

    @Column(name = "evidence", columnDefinition = "TEXT")
    private String evidence;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "evidence_file") // ✅ NEW FIELD
    private String evidenceFile;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}