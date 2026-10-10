package com.hirehub.hirehub_api;

import com.hirehub.hirehub_api.dto.application.ApplyRequest;
import com.hirehub.hirehub_api.dto.application.UpdateApplicationStatusRequest;
import com.hirehub.hirehub_api.dto.job.CreateJobRequest;
import com.hirehub.hirehub_api.enums.ApplicationStatus;
import com.hirehub.hirehub_api.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class HiringLifeCycleE2Test extends BaseIntegrationTest {


    @Test
    @DisplayName("Complete Hiring Workflow: Post Job -> Search -> Apply -> Shortlist -> Interview -> Hire")
    void testCompleteHiringPipeline() throws Exception {
        String recruiterToken = registerAndLogin("Recruiter alpha", "recruiter@gmail.com",
                "recruiter1234", Role.RECRUITER);
        String candidateToken = registerAndLogin("Candidate", "candidate@gmail.com",
                "candidate1234", Role.CANDIDATE);

        CreateJobRequest createJobRequest = new CreateJobRequest("Senior Java Engineer",
                "we are hiring a java engineer",
                "Gwalior",
                new BigDecimal(1200000),
                new BigDecimal(1800000),
                5,
                "FULL_TIME",
                Set.of("Java", "Spring boot", "MySql")
        );
        MvcResult jobResult = mockMvc.perform(post("/api/jobs")
                        .header("Authorization", "Bearer "+recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createJobRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Senior Java Engineer"))
                .andExpect(jsonPath("$.jobStatus").value("OPEN"))
                .andReturn();
        Long jobId = objectMapper.readTree(jobResult.getResponse().getContentAsString()).get("id").asLong();


        mockMvc.perform(get("/api/jobs")
                        .param("keyword", "Java")
                        .param("location", "Gwalior")
                        .param("minExperience", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageContent[0].id").value(jobId))
                .andExpect(jsonPath("$.totalElements").value(1));

        ApplyRequest applyRequest = new ApplyRequest("I Have 3 years experience in java",
                "http://example.com"
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/jobs/" + jobId + "/applications")
                        .header("Authorization", "Bearer "+ candidateToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applyRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andExpect(jsonPath("$.resumeUrl").value("http://example.com"))
                .andReturn();
        Long applicationId = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/jobs/" + jobId + "/applications")
                        .header("Authorization", "Bearer "+candidateToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applyRequest)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/applications/me")
                        .header("Authorization", "Bearer "+ candidateToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(applicationId))
                .andExpect(jsonPath("$[0].status").value("APPLIED"));

        mockMvc.perform(get("/api/recruiter/jobs/" + jobId + "/applications")
                        .header("Authorization", "Bearer "+ recruiterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(applicationId))
                .andExpect(jsonPath("$[0].candidateEmail").value("candidate@gmail.com"));
        mockMvc.perform(patch("/api/applications/" + applicationId + "/status")
                        .header("Authorization", "Bearer "+ recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateApplicationStatusRequest(ApplicationStatus.SHORTLISTED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHORTLISTED"));

        mockMvc.perform(patch("/api/applications/" + applicationId + "/status")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateApplicationStatusRequest(ApplicationStatus.INTERVIEWED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INTERVIEWED"));

        mockMvc.perform(patch("/api/applications/" + applicationId + "/status")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateApplicationStatusRequest(ApplicationStatus.HIRED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("HIRED"));

        mockMvc.perform(patch("/api/applications/" + applicationId + "/withdraw")
                        .header("Authorization", "Bearer " + candidateToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Cross-Recruiter Security Guard: Unauthorized recruiters cannot access other recruiters' applicants")
    void testCrossRecruiterAuthorizationForbidden() throws Exception {

        // Recruiter who owns the job
        String recruiterOwnerToken = registerAndLogin(
                "Owner",
                "owner@gmail.com",
                "owner1234",
                Role.RECRUITER
        );

        // Another recruiter who should NOT have access
        String recruiterIntruderToken = registerAndLogin(
                "Intruder",
                "intruder@gmail.com",
                "intruder1234",
                Role.RECRUITER
        );

        // Owner creates a job
        CreateJobRequest createJobRequest = new CreateJobRequest(
                "Java Developer",
                "We are hiring a Java Developer",
                "Gwalior",
                new BigDecimal(1000000),
                new BigDecimal(1500000),
                2,
                "FULL_TIME",
                Set.of("Java", "Spring Boot")
        );

        MvcResult jobResult = mockMvc.perform(
                        post("/api/jobs")
                                .header("Authorization", "Bearer " + recruiterOwnerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createJobRequest))
                )
                .andExpect(status().isCreated())
                .andReturn();

        // Get the job ID
        Long jobId = objectMapper
                .readTree(jobResult.getResponse().getContentAsString())
                .get("id")
                .asLong();

        // Intruder tries to access Owner's applicants
        mockMvc.perform(
                        get("/api/recruiter/jobs/" + jobId + "/applications")
                                .header("Authorization", "Bearer " + recruiterIntruderToken)
                )
                .andExpect(status().isForbidden());
    }


}
