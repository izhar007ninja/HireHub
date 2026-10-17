package com.hirehub.hirehub_api.controller;


import com.hirehub.hirehub_api.dto.application.ApplicationResponse;
import com.hirehub.hirehub_api.dto.application.ApplyRequest;
import com.hirehub.hirehub_api.dto.application.UpdateApplicationStatusRequest;
import com.hirehub.hirehub_api.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Applications",description = "This is the endpoint used for job applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService){
        this.applicationService = applicationService;
    }

    @PostMapping("/jobs/{jobId}/applications")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Apply for a job",description = "Allows the user to apply for job")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Successfully applied",
            content = @Content(schema = @Schema(implementation = ApplicationResponse.class))
            ),

            @ApiResponse(responseCode = "400",
                    description = "Already applied for job or job doesn't exist")
    })
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
    @Operation(summary = "Get candidate's applications",description = "Candidate can get his applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@AuthenticationPrincipal UserDetails userDetails){
        List<ApplicationResponse> candidateApplications = applicationService.getMyApplication(userDetails.getUsername());
        return ResponseEntity.ok(candidateApplications);
    }

    @PatchMapping("/applications/{id}/withdraw")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Withdraw application",description = "Candidate can withdraw his applications")
    public ResponseEntity<ApplicationResponse> withdraw(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        ApplicationResponse applicationResponse = applicationService.withdrawApplication(id, userDetails.getUsername());
        return ResponseEntity.ok(applicationResponse);
    }

    @GetMapping("/recruiter/jobs/{jobId}/applications")
    @PreAuthorize("hasRole('RECRUITER')")
    @Operation(summary = "Get job applications",description = "Recruiter can get job applications")
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
    @Operation(summary = "Change job status",description = "Recruiter can change job status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id,
                                                            @AuthenticationPrincipal UserDetails userDetails,
                                                            @Valid @RequestBody  UpdateApplicationStatusRequest request
                                                            )
    {
        ApplicationResponse response = applicationService.updateStatus(id,userDetails.getUsername(),request);
        return ResponseEntity.ok(response);
    }











}
