package com.governance.evaluation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "criteria")
public class Criterion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "criterion_id")
    private Long criterionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practice_id", nullable = false)
    private Practice practice;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "level0_description", columnDefinition = "TEXT")
    private String level0Description;
    
    @Column(name = "level1_description", columnDefinition = "TEXT")
    private String level1Description;
    
    @Column(name = "level2_description", columnDefinition = "TEXT")
    private String level2Description;
    
    @Column(name = "level3_description", columnDefinition = "TEXT")
    private String level3Description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Long getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(Long criterionId) {
        this.criterionId = criterionId;
    }

    public Practice getPractice() {
        return practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getLevel0Description() {
        return level0Description;
    }

    public void setLevel0Description(String level0Description) {
        this.level0Description = level0Description;
    }

    public String getLevel1Description() {
        return level1Description;
    }

    public void setLevel1Description(String level1Description) {
        this.level1Description = level1Description;
    }

    public String getLevel2Description() {
        return level2Description;
    }

    public void setLevel2Description(String level2Description) {
        this.level2Description = level2Description;
    }

    public String getLevel3Description() {
        return level3Description;
    }

    public void setLevel3Description(String level3Description) {
        this.level3Description = level3Description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}