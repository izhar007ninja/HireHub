package com.hirehub.hirehub_api.controller;

import com.hirehub.hirehub_api.dto.profile.CandidateProfileRequest;
import com.hirehub.hirehub_api.dto.profile.CandidateProfileResponse;
import com.hirehub.hirehub_api.dto.profile.PublicCandidateResponse;
import com.hirehub.hirehub_api.entity.CandidateProfile;
import com.hirehub.hirehub_api.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidates/me")

public class CandidateProfileController {

    private final ProfileService profileService;

    public CandidateProfileController(ProfileService profileService){
        this.profileService = profileService;
    }

    @GetMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<CandidateProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails){
        CandidateProfileResponse response = profileService.getCandidateProfile(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<CandidateProfileResponse> updateProfile(@Valid @RequestBody CandidateProfileRequest request, @AuthenticationPrincipal UserDetails userDetails){
        CandidateProfileResponse response = profileService.updateCandidateProfile(request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    public ResponseEntity<PublicCandidateResponse> getCandidateById(@PathVariable Long id){
        return ResponseEntity.ok(profileService.getCandidateProfileById(id));
    }



}
