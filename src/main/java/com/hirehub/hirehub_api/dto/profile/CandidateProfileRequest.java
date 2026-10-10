package com.hirehub.hirehub_api.dto.profile;

import com.hirehub.hirehub_api.entity.Skill;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CandidateProfileRequest(
        @Size(max = 20, message = "Phone no. cannot exceed 20 characters")
        String phone,

        @Size(max = 1000,message = "bio cannot exceed 1000 characters")
        String bio,

        @Size(max = 100, message = "Location cannot exceed 100 characters")
        String location,

        @Min(value= 0,message = "experience cannot be negative")
        Integer experienceInYears,

        String resumeUrl,

        String profilePictureUrl,

        Set<String> skills


) {
}
