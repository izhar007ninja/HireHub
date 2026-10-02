package com.hirehub.hirehub_api.dto.application;

import jakarta.validation.constraints.Size;

public record ApplyRequest (

        @Size(max = 2000,message = "Cover letter cannot exceed 2000 characters")
        String coverLetter,

        String resumeUrl

){
}
