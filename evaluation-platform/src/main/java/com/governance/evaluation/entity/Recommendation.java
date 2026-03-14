package com.governance.evaluation.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Data
public class Recommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long recommendationId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;
    
    @Column(name = "principle_id")
    private Integer principleId;
    
    @Column(name = "practice_id")
    private Integer practiceId;
    
    @Column(name = "criterion_id")
    private Integer criterionId;
    
    @Column(name = "current_maturity_level")
    private Integer currentMaturityLevel;
    
    @Column(name = "target_maturity_level")
    private Integer targetMaturityLevel;
    
    @Column(name = "recommendation_text", columnDefinition = "TEXT", nullable = false)
    private String recommendationText;
    
    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    @CreationTimestamp
    @Column(name = "generated_date", updatable = false)
    private LocalDateTime generatedDate;
    
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}