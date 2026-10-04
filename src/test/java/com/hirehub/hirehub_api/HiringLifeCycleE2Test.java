package com.hirehub.hirehub_api;

import com.hirehub.hirehub_api.dto.job.CreateJobRequest;
import com.hirehub.hirehub_api.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
public class HiringLifeCycleE2Test extends BaseIntegrationTest{


    @Test
    @DisplayName("Complete Hiring Workflow: Post Job -> Search -> Apply -> Shortlist -> Interview -> Hire")
    void testCompleteHiringPipeline() throws Exception{
        String recruiterToken = registerAndLogin("Recruiter alpha","recruiter@gmail.com",
                "recruiter1234", Role.RECRUITER);
        String candidateToken = registerAndLogin("Candidate","candidate@gmail.com",
                "candidate1234",Role.CANDIDATE);

        CreateJobRequest createJobRequest = new CreateJobRequest("Senior Java Engineer",
                "we are hiring a java engineer",
                "Gwalior",
                new BigDecimal(1200000),
                new BigDecimal(1800000),
                5,
                "FULL_TIME",
                Set.of("Java","Spring boot","MySql")
                );
        MvcResult jobResult = mockMvc.perform(post("/api/jobs/")
                .header("Authorization","Bearer ",recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createJobRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Senior Java Engineer"))
                .andExpect(jsonPath("$.jobStatus").value("OPEN"))
                .andReturn();
        Long jobId = objectMapper.readTree(jobResult.getResponse().getContentAsString()).get("id").asLong();

    }

}
