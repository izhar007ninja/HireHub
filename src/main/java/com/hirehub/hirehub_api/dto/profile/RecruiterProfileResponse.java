package com.hirehub.hirehub_api.dto.profile;

public record RecruiterProfileResponse(

        Long userId,
        String recruiterName,
        String email,
        String companyName,
        String companyDescription,
        String companyWebsite,
        String location



){
}
