package com.hirehub.hirehub_api.dto.job;

import com.hirehub.hirehub_api.entity.Skill;
import com.hirehub.hirehub_api.enums.JobStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record UpdateJobRequest(
        @NotBlank(message = "title is required")
        @Size(max = 150, message = "title cannot exceed 150 characters")
        String title,

        @NotBlank(message = "description cannot be empty")
        @Size(min = 20,message = "description cannot be less than 20 characters")
        String description,

        @NotBlank(message = "location is required")
        String location,

        @DecimalMin(value = "0.0", inclusive = true,message = "salary cannot be negative")
        BigDecimal minSalary,

        @DecimalMin(value = "0.0", message = "salary cannot be negative")
        BigDecimal maxSalary,

        @Min(value = 0, message = "experience cannot be negative")
        Integer experienceRequired,

        @NotBlank(message = "employment type cannot be blank")
        String employmentType,

        @NotNull(message = "Status is required (OPEN, CLOSED, DRAFT)")
        JobStatus jobStatus,
        Set<String> skills

) {
}
