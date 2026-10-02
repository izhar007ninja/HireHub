package com.hirehub.hirehub_api.controller;


import com.hirehub.hirehub_api.dto.application.ApplicationResponse;
import com.hirehub.hirehub_api.dto.application.ApplyRequest;
import com.hirehub.hirehub_api.service.ApplicationService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService){
        this.applicationService = applicationService;
    }

    @PostMapping("/jobs/{jobId}/applications")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable Long jobId,
                                                     @AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody(required = false)ApplyRequest applyRequest
                                                     ) throws BadRequestException {
        ApplicationResponse applicationResponse = applicationService
                .applyToJob(jobId,userDetails.getUsername(),applyRequest
                );

        return ResponseEntity.ok(applicationResponse);
    }



}
