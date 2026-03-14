package com.governance.evaluation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("Organization")  // ✅ ADD THIS
@Data
@EqualsAndHashCode(callSuper = true)
public class Organization extends User {
    
    @Column(name = "date_of_foundation")
    private LocalDate dateOfFoundation;
    
    private String sector;
    
    private String address;
    
    private String phone;
}