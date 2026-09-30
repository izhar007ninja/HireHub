package com.hirehub.hirehub_api.service;

import com.hirehub.hirehub_api.dto.auth.LogInRequest;
import com.hirehub.hirehub_api.dto.auth.LogInResponse;
import com.hirehub.hirehub_api.dto.auth.RegisterRequest;
import com.hirehub.hirehub_api.entity.CandidateProfile;
import com.hirehub.hirehub_api.entity.RecruiterProfile;
import com.hirehub.hirehub_api.entity.User;
import com.hirehub.hirehub_api.enums.Role;
import com.hirehub.hirehub_api.repository.CandidateProfileRepository;
import com.hirehub.hirehub_api.repository.UserRepository;
import com.hirehub.hirehub_api.repository.RecruiterProfileRepository;
import com.hirehub.hirehub_api.security.CustomUserDetails;
import com.hirehub.hirehub_api.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       CandidateProfileRepository candidateProfileRepository,
                       RecruiterProfileRepository recruiterProfileRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public void register(RegisterRequest request){
        if (userRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("Email already exist");
        }

        User user = new User(request.name(), passwordEncoder.encode(request.password()),
                request.email(), request.role(),true);
        User savedUser = userRepository.save(user);

        if(request.role()== Role.CANDIDATE){
            candidateProfileRepository.save(CandidateProfile.builder().user(savedUser).build());
        }
        else if (request.role()==Role.RECRUITER){
            recruiterProfileRepository.save(RecruiterProfile.builder().user(savedUser).build());
        }

    }

    public LogInResponse login(LogInRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(),request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(()->new UsernameNotFoundException("User of this email is not found"));

        UserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails, user.getRole());
        return new LogInResponse(token,"Bearer",user.getRole());
    }



}
