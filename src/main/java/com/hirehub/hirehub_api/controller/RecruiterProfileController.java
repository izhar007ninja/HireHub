package com.hirehub.hirehub_api.controller;

import com.hirehub.hirehub_api.dto.profile.CandidateProfileRequest;
import com.hirehub.hirehub_api.dto.profile.CandidateProfileResponse;
import com.hirehub.hirehub_api.dto.profile.RecruiterProfileRequest;
import com.hirehub.hirehub_api.dto.profile.RecruiterProfileResponse;
import com.hirehub.hirehub_api.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiters/me")
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterProfileController {

    private final ProfileService profileService;

    public RecruiterProfileController(ProfileService profileService){
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<RecruiterProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(profileService.getRecruiterProfile(userDetails.getUsername()));
    }


    @PutMapping
    public ResponseEntity<RecruiterProfileResponse> updateProfile(@Valid @RequestBody RecruiterProfileRequest request, @AuthenticationPrincipal UserDetails userDetails){
        RecruiterProfileResponse response = profileService.updateRecruiterProfile(request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }



}
