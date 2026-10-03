package com.hirehub.hirehub_api.service;
import com.hirehub.hirehub_api.dto.application.ApplicationResponse;
import com.hirehub.hirehub_api.dto.application.ApplyRequest;
import com.hirehub.hirehub_api.dto.application.UpdateApplicationStatusRequest;
import com.hirehub.hirehub_api.entity.*;
import com.hirehub.hirehub_api.enums.ApplicationStatus;
import com.hirehub.hirehub_api.enums.JobStatus;
import com.hirehub.hirehub_api.enums.Role;
import com.hirehub.hirehub_api.exception.ResourceNotFoundException;
import com.hirehub.hirehub_api.exception.UnauthorizedException;
import com.hirehub.hirehub_api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hirehub.hirehub_api.exception.BadRequestException;

import java.util.List;


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

    @Transactional
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

    @Transactional
    public List<ApplicationResponse> getMyApplication(String candidateEmail){
        User candidate = userRepository.findByEmail(candidateEmail)
                .orElseThrow(()->
                        new ResourceNotFoundException("candidate with email"+candidateEmail+"is not found"));

        if (candidate.getRole()!=Role.CANDIDATE){
            throw new BadRequestException("Only candidate can fetch the applications");
        }

        List<ApplicationResponse> candidatesApplication = applicationRepository
                .findByCandidateIdOrderByAppliedAtDesc(candidate.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
        return candidatesApplication;

    }

    @Transactional
    public ApplicationResponse withdrawApplication(Long applicationId,String candidateEmail){
        User candidate = userRepository.findByEmail(candidateEmail)
                .orElseThrow(()->new ResourceNotFoundException("candidate with email"+candidateEmail+"is not found"));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(()->new ResourceNotFoundException("application is not found"));
        if (!candidate.getId().equals(application.getCandidate().getId())){
            throw new UnauthorizedException("user is not authorized");
        }
        if (application.getApplicationStatus()== ApplicationStatus.HIRED){
            throw new BadRequestException("user is already hired");
        }
        if (application.getApplicationStatus()==ApplicationStatus.WITHDRAWN){
            throw new BadRequestException("user had already withdrawn the application");
        }

        if (application.getApplicationStatus()==ApplicationStatus.REJECTED){
            throw new BadRequestException("Application is rejected");
        }
        application.setApplicationStatus(ApplicationStatus.WITHDRAWN);
        Application updatedApplication = applicationRepository.save(application);
        return mapToResponse(updatedApplication);

    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, String recruiterEmail, UpdateApplicationStatusRequest request){
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(()->new UnauthorizedException("recruiter doesn't exist"));
        if (recruiter.getRole()!=Role.RECRUITER){
            throw new UnauthorizedException("Only recruiter can update the job status");
        }
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(()->new ResourceNotFoundException("Application was not found"));

        if (!application.getJob().getRecruiter().getId().equals(recruiter.getId())){
            throw new UnauthorizedException("You cannot modify the status of this job you are unauthorized");
        }
        if(application.getApplicationStatus()==ApplicationStatus.WITHDRAWN){
            throw new BadRequestException("this application is already withdrawn");
        }
        if (request.status()==ApplicationStatus.WITHDRAWN){
            throw new BadRequestException("Recruiters cannot withdraw applications");
        }
        application.setApplicationStatus(request.status());
        applicationRepository.save(application);
        return mapToResponse(application);


    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForJob(Long jobId,String recruiterEmail){
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(()->new UnauthorizedException("recruiter doesn't exist"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(()->new ResourceNotFoundException("job is not found"));
        if (recruiter.getRole()!=Role.RECRUITER){
            throw new UnauthorizedException("Only recruiters can get the applications");
        }

        if (!job.getRecruiter().getId().equals(recruiter.getId())){
            throw new UnauthorizedException("You are not authorized to get the applications");
        }

        List<ApplicationResponse> applications = applicationRepository
                .findByJobIdOrderByAppliedAtDesc(jobId)
                .stream()
                .map(this::mapToResponse)
                .toList();
        return applications;

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
