package com.hirehub.hirehub_api.controller;

import com.hirehub.hirehub_api.dto.auth.LogInRequest;
import com.hirehub.hirehub_api.dto.auth.LogInResponse;
import com.hirehub.hirehub_api.dto.auth.RegisterRequest;
import com.hirehub.hirehub_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register( @Valid @RequestBody RegisterRequest registerRequest){
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User Created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LogInResponse> login(@Valid @RequestBody LogInRequest logInRequest){
        LogInResponse login = authService.login(logInRequest);
        return ResponseEntity.ok(login);
    }


}
