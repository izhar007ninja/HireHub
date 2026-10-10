package com.hirehub.hirehub_api.dto.application;

import com.hirehub.hirehub_api.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateApplicationStatusRequest(
        @NotNull(message = "Application status is required")
        ApplicationStatus status
){
}
