package com.hirehub.hirehub_api.controller;

import com.hirehub.hirehub_api.dto.auth.LogInRequest;
import com.hirehub.hirehub_api.dto.auth.LogInResponse;
import com.hirehub.hirehub_api.dto.auth.RegisterRequest;
import com.hirehub.hirehub_api.dto.error.ErrorResponse;
import com.hirehub.hirehub_api.dto.error.ValidationErrorResponse;
import com.hirehub.hirehub_api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication",description = "User can authenticate")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "user can register")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "User registered successfully"),
            @ApiResponse(responseCode = "401",description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(responseCode = "409",description = "Email already exist",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )

    })
    public ResponseEntity<String> register( @Valid @RequestBody RegisterRequest registerRequest){
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User Created successfully");
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Login user",description = "user can login")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "user logged in",
            content = @Content(schema = @Schema(implementation = LogInResponse.class))
            ),
            @ApiResponse(responseCode = "409",description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            )
    })
    public ResponseEntity<LogInResponse> login(@Valid @RequestBody LogInRequest logInRequest){
        LogInResponse login = authService.login(logInRequest);
        return ResponseEntity.ok(login);
    }


}
