package com.hirehub.hirehub_api.service;


import com.hirehub.hirehub_api.dto.admin.AdminUserSummaryResponse;
import com.hirehub.hirehub_api.dto.admin.PlatformStatusResponse;
import com.hirehub.hirehub_api.dto.common.PageResponse;
import com.hirehub.hirehub_api.dto.job.JobResponse;
import com.hirehub.hirehub_api.entity.Job;
import com.hirehub.hirehub_api.entity.Skill;
import com.hirehub.hirehub_api.entity.User;
import com.hirehub.hirehub_api.enums.JobStatus;
import com.hirehub.hirehub_api.enums.Role;
import com.hirehub.hirehub_api.exception.BadRequestException;
import com.hirehub.hirehub_api.exception.ResourceNotFoundException;
import com.hirehub.hirehub_api.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final ApplicationRepository applicationRepository;


    public AdminService(JobRepository jobRepository, UserRepository userRepository, CandidateProfileRepository candidateProfileRepository, RecruiterProfileRepository recruiterProfileRepository, ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public PlatformStatusResponse getPlatformStatus(){

        long totalUsers = userRepository.count();
        long totalCandidates = candidateProfileRepository.count();
        long totalRecruiters = recruiterProfileRepository.count();
        long totalJobs = jobRepository.count();
        long openJobs = jobRepository.countByJobStatus(JobStatus.OPEN);
        long totalApplications = applicationRepository.count();

        return new PlatformStatusResponse(totalUsers,totalCandidates,totalRecruiters,totalJobs,
                openJobs,totalApplications);

    }


    @Transactional(readOnly = true)
    public PageResponse<AdminUserSummaryResponse> getAllUsers(Pageable pageable){
        Page<AdminUserSummaryResponse> userResponses =  userRepository.findAll(pageable)
                .map(r-> new AdminUserSummaryResponse(r.getId(),r.getName(),r.getEmail(),r.getRole(),
                        r.isEnabled(),r.getCreatedAt()
                        ));

        return PageResponse.of(userResponses);
    }



    @Transactional
    public AdminUserSummaryResponse toggleUserStatus(Long id,String adminEmail){
        User user = userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("user was not found"));

        if(user.getRole()== Role.ADMIN){
            throw new BadRequestException("can't ban or unban admin");
        }

        if (user.getEmail().equals(adminEmail)){
            throw new BadRequestException("Admin can't ban himself");
        }

        if (user.isEnabled()){
            user.setEnabled(false);
        }
        else{
            user.setEnabled(true);
        }

        userRepository.save(user);

return new AdminUserSummaryResponse(user.getId(), user.getName(), user.getEmail(),user.getRole(),
        user.isEnabled(),user.getCreatedAt());
    }


    @Transactional
    public JobResponse forceCloseJob(Long jobId){
        Job job = jobRepository.findById(jobId)
                .orElseThrow(()->new ResourceNotFoundException("job not found"));

        if (job.getJobStatus()==JobStatus.CLOSED){
            throw new BadRequestException("Job is already closed");
        }

        job.setJobStatus(JobStatus.CLOSED);

        return new JobResponse(job.getId(),job.getTitle(),job.getDescription(),job.getLocation(),
                job.getEmploymentType(),job.getMinimumSalary(),job.getMaximumSalary(),
                job.getExperienceRequired(),job.getRecruiter().getId(),job.getRecruiter().getName(),
                job.getJobStatus(),job.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()),
                job.getCreatedAt()
                );


    }




}
