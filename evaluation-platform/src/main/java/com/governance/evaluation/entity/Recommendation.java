package com.governance.evaluation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Data
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long recommendationId;

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

    @Column(name = "current_maturity_level")
    private Integer currentMaturityLevel;

    @Column(name = "target_maturity_level")
    private Integer targetMaturityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private RecommendationPriority priority;

    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "action_plan", columnDefinition = "TEXT")
    private String actionPlan;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}