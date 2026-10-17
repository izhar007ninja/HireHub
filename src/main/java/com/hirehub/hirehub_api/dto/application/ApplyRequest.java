package com.hirehub.hirehub_api.dto.application;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Job application submission payload")
public record ApplyRequest (

        @Schema(description = "Cover letter give summary of your resume ")
        @Size(max = 2000,message = "Cover letter cannot exceed 2000 characters")
        String coverLetter,

        @Schema(description = "Resume defines your skills")
        String resumeUrl

){
}
