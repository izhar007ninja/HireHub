package com.hirehub.hirehub_api.dto.profile;

import com.hirehub.hirehub_api.entity.Skill;

import java.util.Set;

public record CandidateProfileResponse(
        Long userId,
        String phone,
        String email,
        String bio,
        String resumeUrl,
        Integer experienceInYears,
        String location,
        String profilePictureUrl,
        Set<String> skills

) {
}
