package com.governance.evaluation.dto.organization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {
	 private Long userId;
	    private Long organizationId;
	    private String name;
	    private String email;
	    private LocalDate dateOfFoundation;
	    private String sector;
	    private String address;
	    private String phone;
	    private Boolean isActive;
	    private LocalDateTime createdAt;

}
