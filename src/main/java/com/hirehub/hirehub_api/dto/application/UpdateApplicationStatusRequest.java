package com.hirehub.hirehub_api.dto.application;

import com.hirehub.hirehub_api.enums.ApplicationStatus;

public record UpdateApplicationStatusRequest(
        ApplicationStatus status
){
}
