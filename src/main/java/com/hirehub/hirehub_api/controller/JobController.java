package com.hirehub.hirehub_api.controller;


import com.hirehub.hirehub_api.dto.job.CreateJobRequest;
import com.hirehub.hirehub_api.dto.job.JobResponse;
import com.hirehub.hirehub_api.dto.job.UpdateJobRequest;
import com.hirehub.hirehub_api.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService){
        this.jobService = jobService;
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest jobRequest,
                                                 @AuthenticationPrincipal UserDetails userDetails){
        JobResponse jobResponse = jobService.createJob(jobRequest, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(jobResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id){
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllOpenJobs(){
        List<JobResponse> allOpenJobs = jobService.getAllOpenJobs();
        return ResponseEntity.ok(allOpenJobs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UpdateJobRequest jobRequest){
        JobResponse jobResponse = jobService.updateJob(id,userDetails.getUsername(),jobRequest);
        return ResponseEntity.ok(jobResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> deleteJob(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        jobService.deleteJob(id,userDetails.getUsername());
        return ResponseEntity.ok("Job get deleted");
    }



}
