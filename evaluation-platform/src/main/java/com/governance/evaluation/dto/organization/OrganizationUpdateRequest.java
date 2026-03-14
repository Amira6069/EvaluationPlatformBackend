package com.governance.evaluation.dto.organization;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data

public class OrganizationUpdateRequest {
	 @Size(max = 100)
	    private String name;
	    
	    private LocalDate dateOfFoundation;
	    
	    @Size(max = 100)
	    private String sector;
	    
	    @Size(max = 200)
	    private String address;
	    
	    @Size(max = 20)
	    private String phone;

}
