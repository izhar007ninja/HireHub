package com.hirehub.hirehub_api.service;
import com.hirehub.hirehub_api.dto.application.ApplicationResponse;
import com.hirehub.hirehub_api.dto.application.ApplyRequest;
import com.hirehub.hirehub_api.entity.*;
import com.hirehub.hirehub_api.enums.ApplicationStatus;
import com.hirehub.hirehub_api.enums.JobStatus;
import com.hirehub.hirehub_api.enums.Role;
import com.hirehub.hirehub_api.exception.ResourceNotFoundException;
import com.hirehub.hirehub_api.exception.UnauthorizedException;
import com.hirehub.hirehub_api.repository.*;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;


@Service
public class ApplicationService {
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;


    public ApplicationService(CandidateProfileRepository candidateProfileRepository,
                              UserRepository userRepository,
                              JobRepository jobRepository,
                              ApplicationRepository applicationRepository) {
        this.candidateProfileRepository = candidateProfileRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }
    public ApplicationResponse applyToJob(Long jobId, String candidateEmail, ApplyRequest applyRequest) throws BadRequestException {
        User candidate = userRepository.findByEmail(candidateEmail)
                .orElseThrow(()->new ResourceNotFoundException("User with "+candidateEmail+" is not found"));

        if (candidate.getRole()!= Role.CANDIDATE){
            throw  new UnauthorizedException("This user not a candidate");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(()->new ResourceNotFoundException("Job with id " +jobId+" is not found"));

        if (job.getJobStatus()!= JobStatus.OPEN){
            throw new BadRequestException("this job is not open");
        }

        if (applicationRepository.existsByJobIdAndCandidateId(jobId, candidate.getId())){
            throw new BadRequestException("You have already applied for this job");
        }

        String resolvedResumeUrl = null;

        if (applyRequest!=null && applyRequest.resumeUrl()!=null && !applyRequest.resumeUrl().trim().isBlank()){
            resolvedResumeUrl = applyRequest.resumeUrl().trim();
        }
        else {
            resolvedResumeUrl = candidateProfileRepository.findByUserId(candidate.getId()).map(CandidateProfile::getResumeUrl).orElse(null);
        }


        if (resolvedResumeUrl==null || resolvedResumeUrl.isBlank()){
            throw  new BadRequestException("please upload a resume in your profile or in application request");
        }
        Application application = Application.builder()
                .job(job)
                .candidate(candidate)
                .coverLetter(applyRequest !=null ? applyRequest.coverLetter():null)
                .resumeUrl(resolvedResumeUrl)
                .applicationStatus(ApplicationStatus.APPLIED)
                .build();
        applicationRepository.save(application);
        return mapToResponse(application);





    }

    private ApplicationResponse mapToResponse(Application application){
        ApplicationResponse applicationResponse = new ApplicationResponse(application.getId()
                ,application.getJob().getId(),
                application.getJob().getTitle(),
                application.getJob().getRecruiter().getName()
                ,application.getCandidate().getId(),
                application.getCandidate().getName(),
                application.getCandidate().getEmail(),
                application.getApplicationStatus(),
                application.getCoverLetter(),
                application.getResumeUrl(),
                application.getAppliedAt(),
                application.getUpdateAt()
                );
        return applicationResponse;
    }



}
