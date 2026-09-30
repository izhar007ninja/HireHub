package com.hirehub.hirehub_api.dto.job;

import com.hirehub.hirehub_api.enums.JobStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record JobResponse(
        Long id,
        String title,
        String description,
        String location,
        String employmentType,
        BigDecimal minSalary,
        BigDecimal maxSalary,
        Integer experienceRequired,
        Long recruiterId,
        String recruiterName,
        JobStatus jobStatus,
        LocalDateTime createdAt


) {
}
