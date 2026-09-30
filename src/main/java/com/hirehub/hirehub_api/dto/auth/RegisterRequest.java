package com.hirehub.hirehub_api.dto.auth;

import com.hirehub.hirehub_api.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8,message = "password must be at least 8 characters") String password,
        @NotNull Role role
        ) {}
