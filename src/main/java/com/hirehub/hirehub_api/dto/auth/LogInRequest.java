package com.hirehub.hirehub_api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LogInRequest(
        @NotBlank @Email String email,
        @NotBlank  String password

)  {
}
