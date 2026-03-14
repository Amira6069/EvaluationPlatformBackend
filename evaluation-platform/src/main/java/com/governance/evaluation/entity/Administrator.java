package com.governance.evaluation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("Administrator")  // ✅ ADD THIS
@Data
@EqualsAndHashCode(callSuper = true)
public class Administrator extends User {
    
    // Admin-specific fields (if any)
    // Currently empty, but class is needed for inheritance
}
