package com.hirehub.hirehub_api.controller;


import com.hirehub.hirehub_api.dto.application.ApplicationResponse;
import com.hirehub.hirehub_api.dto.application.ApplyRequest;
import com.hirehub.hirehub_api.dto.application.UpdateApplicationStatusRequest;
import com.hirehub.hirehub_api.service.ApplicationService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService){
        this.applicationService = applicationService;
    }

    @PostMapping("/jobs/{jobId}/applications")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable Long jobId,
                                                     @AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody(required = false)ApplyRequest applyRequest
                                                     )  {
        ApplicationResponse applicationResponse = applicationService
                .applyToJob(jobId,userDetails.getUsername(),applyRequest
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(applicationResponse);
    }

    @GetMapping("/applications/me")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@AuthenticationPrincipal UserDetails userDetails){
        List<ApplicationResponse> candidateApplications = applicationService.getMyApplication(userDetails.getUsername());
        return ResponseEntity.ok(candidateApplications);
    }

    @PatchMapping("/applications/{id}/withdraw")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationResponse> withdraw(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        ApplicationResponse applicationResponse = applicationService.withdrawApplication(id, userDetails.getUsername());
        return ResponseEntity.ok(applicationResponse);
    }

    @GetMapping("/recruiter/jobs/{jobId}/applications")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<ApplicationResponse>>
    getApplicationsForJob(@PathVariable Long jobId,
                          @AuthenticationPrincipal UserDetails userDetails

                          )
    {
     List<ApplicationResponse> applicationResponses = applicationService.
             getApplicationsForJob(jobId, userDetails.getUsername());
     return ResponseEntity.ok(applicationResponses);
    }

    @PatchMapping("/applications/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id,
                                                            @AuthenticationPrincipal UserDetails userDetails,
                                                            @Valid @RequestBody  UpdateApplicationStatusRequest request
                                                            )
    {
        ApplicationResponse response = applicationService.updateStatus(id,userDetails.getUsername(),request);
        return ResponseEntity.ok(response);
    }











}
