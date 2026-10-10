package com.hirehub.hirehub_api.dto.profile;

import com.hirehub.hirehub_api.entity.Skill;

import java.util.Set;

public record PublicCandidateResponse(
        Long candidateId,
        String name,
        String email,
        String phone,
        String bio,
        String location,
        Integer experience,
        String resumeUrl,
        Set<String> skills



) {
}
