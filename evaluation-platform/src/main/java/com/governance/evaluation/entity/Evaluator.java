package com.governance.evaluation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("Evaluator")  // ✅ ADD THIS
@Data
@EqualsAndHashCode(callSuper = true)
public class Evaluator extends User {
    
    private String department;
    
    @Column(name = "role_level")
    private String roleLevel;
    
    @Column(name = "certification_level")
    private String certificationLevel;
    
    @Column(name = "domain_of_expertise")
    private String domainOfExpertise;
}