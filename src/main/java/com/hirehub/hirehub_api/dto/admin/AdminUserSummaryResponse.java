package com.hirehub.hirehub_api.dto.admin;

import com.hirehub.hirehub_api.enums.Role;

import java.time.LocalDateTime;

public record AdminUserSummaryResponse (
        Long id,
        String name,
        String email,
        Role role,
        boolean isEnabled,
        LocalDateTime createdAt
){
}
