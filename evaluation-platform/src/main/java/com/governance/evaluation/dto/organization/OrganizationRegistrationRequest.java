package com.governance.evaluation.dto.organization;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data

public class OrganizationRegistrationRequest {
	  @NotBlank(message = "Name is required")
	    @Size(max = 100)
	    private String name;
	    
	    @NotBlank(message = "Email is required")
	    @Email(message = "Invalid email format")
	    private String email;
	    
	    @NotBlank(message = "Password is required")
	    @Size(min = 6, message = "Password must be at least 6 characters")
	    private String password;
	    
	    private LocalDate dateOfFoundation;
	    
	    @Size(max = 100)
	    private String sector;
	    
	    @Size(max = 200)
	    private String address;
	    
	    @Size(max = 20)
	    private String phone;

}
