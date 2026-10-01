package com.hirehub.hirehub_api.dto.job;

import com.hirehub.hirehub_api.entity.Skill;
import com.hirehub.hirehub_api.enums.JobStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

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
        Set<String> skills,
        LocalDateTime createdAt



) {
}
