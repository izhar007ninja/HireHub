package com.hirehub.hirehub_api.controller;


import com.hirehub.hirehub_api.dto.profile.PublicCompanyResponse;
import com.hirehub.hirehub_api.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final ProfileService profileService;

    public CompanyController(ProfileService profileService) {
        this.profileService = profileService;
    }


    @GetMapping
    public ResponseEntity<List<PublicCompanyResponse>> getAllCompanies(){
        return ResponseEntity.ok(profileService.getAllCompanies());
    }

    @GetMapping("/{recruiterId}")
    public ResponseEntity<PublicCompanyResponse> getCompany(@PathVariable Long recruiterId){
        return ResponseEntity.ok(profileService.getCompanyByRecruiterId(recruiterId));
    }

}
