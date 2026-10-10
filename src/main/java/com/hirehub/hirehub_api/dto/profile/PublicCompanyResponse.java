package com.hirehub.hirehub_api.dto.profile;

import com.hirehub.hirehub_api.dto.job.JobResponse;

import java.util.List;

public record PublicCompanyResponse(
        Long recruiterId,
        String recruiterName,
        String companyName,
        String companyDescription,
        String companyWebsite,
        String location,
        List<JobResponse> activeJobs
) {
}
