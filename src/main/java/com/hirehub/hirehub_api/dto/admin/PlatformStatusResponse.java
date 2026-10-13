package com.hirehub.hirehub_api.dto.admin;

public record PlatformStatusResponse(
        long totalUsers,
        long totalCandidates,
        long totalRecruiters,
        long totalJobs,
        long openJobs,
        long totalApplications
) {
}
