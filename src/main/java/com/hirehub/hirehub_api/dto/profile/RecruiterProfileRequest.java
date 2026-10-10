package com.hirehub.hirehub_api.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecruiterProfileRequest (
        @NotBlank(message = "Company name is required")
        @Size(max = 150, message = "Company name cannot exceed 150 characters")
        String companyName,

        @Size(max = 2000, message = "Company description cannot exceed 2000 characters")
        String companyDescription,

        String companyWebsite,

        @Size(max = 100, message = "Location cannot exceed 100 characters")
        String location
){
}
