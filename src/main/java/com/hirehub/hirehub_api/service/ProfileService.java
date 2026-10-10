package com.hirehub.hirehub_api.service;

import com.hirehub.hirehub_api.dto.job.JobResponse;
import com.hirehub.hirehub_api.dto.profile.*;
import com.hirehub.hirehub_api.entity.*;
import com.hirehub.hirehub_api.enums.JobStatus;
import com.hirehub.hirehub_api.enums.Role;
import com.hirehub.hirehub_api.exception.BadRequestException;
import com.hirehub.hirehub_api.exception.ResourceNotFoundException;
import com.hirehub.hirehub_api.exception.UnauthorizedException;
import com.hirehub.hirehub_api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;


    public ProfileService(UserRepository userRepository, CandidateProfileRepository candidateProfileRepository, RecruiterProfileRepository recruiterProfileRepository, SkillRepository skillRepository,JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
    }



    @Transactional(readOnly = true)
    public CandidateProfileResponse getCandidateProfile(String email){
        User user = getAuthenticatedUser(email,Role.CANDIDATE);
        CandidateProfile candidateProfile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(()->new ResourceNotFoundException("Candidate profile is not found"))
                ;
        return mapToCandidateResponse(candidateProfile);
    }

    @Transactional(readOnly = true)
    public RecruiterProfileResponse getRecruiterProfile(String email){
        User user = getAuthenticatedUser(email,Role.RECRUITER);

        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(user.getId())
                .orElseThrow(()->new ResourceNotFoundException("Recruiter profile is not found"));


        return mapToRecruiterProfileResponse(recruiterProfile);
    }

    @Transactional
    public CandidateProfileResponse updateCandidateProfile(CandidateProfileRequest request,String email){
        User user = getAuthenticatedUser(email,Role.CANDIDATE);
        CandidateProfile candidateProfile = candidateProfileRepository.findByUserId(user.getId())
                .orElseGet(()->CandidateProfile.builder().user(user).build());
        candidateProfile.setPhone(request.phone());
        candidateProfile.setBio(request.bio());
        candidateProfile.setLocation(request.location());
        candidateProfile.setProfilePictureUrl(request.profilePictureUrl());
        candidateProfile.setResumeUrl(request.resumeUrl());
        candidateProfile.setExperienceYears(request.experienceInYears());

        if (request.skills()!=null){
            Set<Skill> skills = request.skills().stream().filter(s->s!=null && !s.isBlank())
                    .map(String::trim)
                    .map(name->skillRepository.findByNameIgnoreCase(name)
                            .orElseGet(()->skillRepository.save(new Skill(name))))
                    .collect(Collectors.toSet());

            candidateProfile.setSkills(skills);
        }
        candidateProfileRepository.save(candidateProfile);
        return mapToCandidateResponse(candidateProfile);

    }

    @Transactional
    public RecruiterProfileResponse updateRecruiterProfile(RecruiterProfileRequest request,String email){
        User user = getAuthenticatedUser(email,Role.RECRUITER);
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(user.getId())
                .orElseGet(()-> RecruiterProfile.builder().user(user).build());
        recruiterProfile.setLocation(request.location());
        recruiterProfile.setCompanyName(request.companyName());
        recruiterProfile.setCompanyDescription(request.companyDescription());
        recruiterProfile.setCompanyWebsite(request.companyWebsite());

        recruiterProfileRepository.save(recruiterProfile);
        return mapToRecruiterProfileResponse(recruiterProfile);

    }

    @Transactional(readOnly = true)
    public List<PublicCompanyResponse> getAllCompanies(){
        return recruiterProfileRepository.findAllWithCompanies().stream().map(this::mapToPublicCompanyResponse).toList();
    }

    @Transactional(readOnly = true)
    public PublicCompanyResponse getCompanyByRecruiterId(Long recruiterId){
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiterId)
                .orElseThrow(()->new ResourceNotFoundException("Company not found"));
        return mapToPublicCompanyResponse(recruiterProfile);
    }

    @Transactional(readOnly = true)
    public PublicCandidateResponse getCandidateProfileById(Long candidateId){
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));

        if(candidate.getRole()!=Role.CANDIDATE){
            throw new UnauthorizedException("User is not a candidate");
        }
        CandidateProfile candidateProfile = candidateProfileRepository.findByUserId(candidateId)
                .orElseThrow(()->new ResourceNotFoundException("candidate profile not found"));

        Set<String> skills = candidateProfile.getSkills()!=null ? candidateProfile.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()) :
                Collections.emptySet()
                ;
        return new PublicCandidateResponse(candidateId,candidate.getName(),candidate.getEmail(),
                candidateProfile.getPhone(),candidateProfile.getBio(),candidateProfile.getLocation(),
                candidateProfile.getExperienceYears(),candidateProfile.getResumeUrl(),skills
                );

    }




    private PublicCompanyResponse mapToPublicCompanyResponse(RecruiterProfile recruiterProfile){

        List<Job> jobs = jobRepository.findByRecruiterIdAndJobStatus(recruiterProfile.getUser().getId(), JobStatus.OPEN);
        List<JobResponse> jobResponses = jobs.stream().map(
                job-> new JobResponse(job.getId(),job.getTitle(),job.getDescription(),
                        job.getLocation(),
                        job.getEmploymentType(),
                        job.getMinimumSalary(),
                        job.getMaximumSalary(),
                        job.getExperienceRequired(),
                        job.getRecruiter().getId(),
                        job.getRecruiter().getName(),
                        job.getJobStatus(),
                        job.getSkills() !=null ? job.getSkills().stream().map(Skill::getName).collect(Collectors.toSet())
                                : Collections.emptySet(),
                        job.getCreatedAt()
                        )
                ).toList();
        return new PublicCompanyResponse(recruiterProfile.getUser().getId(),
                recruiterProfile.getUser().getName(),
                recruiterProfile.getCompanyName(),
                recruiterProfile.getCompanyDescription(),
                recruiterProfile.getCompanyWebsite(),
                recruiterProfile.getLocation(),
                jobResponses
                );
    }






    private User getAuthenticatedUser(String email, Role expectedRole){
        User user = userRepository.findByEmail(email).
                orElseThrow(()->new ResourceNotFoundException("User with email "+email+" is not found"));
        if (user.getRole()!=expectedRole){
            throw new UnauthorizedException("user is not authorized");
        }

        return user;
    }

    private CandidateProfileResponse mapToCandidateResponse(CandidateProfile candidateProfile){

        Set<String> skills = candidateProfile.getSkills().stream().map(Skill::getName).collect(Collectors.toSet());
        CandidateProfileResponse candidateProfileResponse =
                new CandidateProfileResponse(candidateProfile.getUser().getId(),
                        candidateProfile.getPhone(),
                        candidateProfile.getUser().getEmail(),
                        candidateProfile.getBio(),
                        candidateProfile.getResumeUrl(),
                        candidateProfile.getExperienceYears(),
                        candidateProfile.getLocation(),
                        candidateProfile.getProfilePictureUrl(),
                        skills
                        );
        return candidateProfileResponse;
    }

    private RecruiterProfileResponse mapToRecruiterProfileResponse(RecruiterProfile recruiterProfile){
        RecruiterProfileResponse recruiterProfileResponse =
                new RecruiterProfileResponse(recruiterProfile.getUser().getId()
                ,recruiterProfile.getUser().getName(),
                        recruiterProfile.getUser().getEmail(),
                        recruiterProfile.getCompanyName(),
                        recruiterProfile.getCompanyDescription(),
                        recruiterProfile.getCompanyWebsite(),
                        recruiterProfile.getLocation()
                );
        return recruiterProfileResponse;
    }





}
