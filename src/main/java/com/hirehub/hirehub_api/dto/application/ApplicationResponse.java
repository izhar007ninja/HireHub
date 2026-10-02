package com.hirehub.hirehub_api.dto.application;

import com.hirehub.hirehub_api.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponse (
        Long id,
        Long jobId,
        String jobTitle,
        String companyName,
        Long candidateId,
        String candidateName,
        String candidateEmail,
        ApplicationStatus status,
        String coverLetter,
        String resumeUrl,
        LocalDateTime appliedAt,

        LocalDateTime updateAt){
}
