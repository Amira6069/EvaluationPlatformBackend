package com.governance.evaluation.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_results")
@Data
public class EvaluationResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evaluation_id", unique = true, nullable = false)
    private Evaluation evaluation;
    
    @Column(name = "final_score", nullable = false)
    private Double finalScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "certification_label", nullable = false)
    private CertificationLabel certificationLabel;
    
    @Column(name = "is_certified", nullable = false)
    private Boolean isCertified;
    
    @CreationTimestamp
    @Column(name = "issued_date", updatable = false)
    private LocalDateTime issuedDate;
    
    @Column(name = "valid_until")
    private LocalDateTime validUntil;
    
    public enum CertificationLabel {
        NOT_CERTIFIED,
        CERTIFIED_BRONZE,
        CERTIFIED_SILVER,
        CERTIFIED_GOLD,
        CERTIFIED_PLATINUM
    }
}