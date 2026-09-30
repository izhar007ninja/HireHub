package com.hirehub.hirehub_api.dto.auth;

import com.hirehub.hirehub_api.enums.Role;

public record LogInResponse(
        String token,
        String type,
        Role role
) {
}
