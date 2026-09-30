package com.hirehub.hirehub_api.service;

import com.hirehub.hirehub_api.dto.job.CreateJobRequest;
import com.hirehub.hirehub_api.dto.job.JobResponse;
import com.hirehub.hirehub_api.dto.job.UpdateJobRequest;
import com.hirehub.hirehub_api.entity.Job;
import com.hirehub.hirehub_api.entity.User;
import com.hirehub.hirehub_api.enums.JobStatus;
import com.hirehub.hirehub_api.enums.Role;
import com.hirehub.hirehub_api.exception.ResourceNotFoundException;
import com.hirehub.hirehub_api.exception.UnauthorizedException;
import com.hirehub.hirehub_api.repository.JobRepository;
import com.hirehub.hirehub_api.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public JobService(UserRepository userRepository,JobRepository jobRepository){
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }


    @Transactional(readOnly = true)
    public List<JobResponse> getAllOpenJobs(){
        List<JobResponse> jobs = jobRepository.findByJobStatus(JobStatus.OPEN)
                .stream().map(this::mapToResponse)
                .toList();
        return jobs;
    }


    @Transactional
    public JobResponse createJob(CreateJobRequest jobRequest,String recruiterEmail){
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(()->
                        new UsernameNotFoundException("Recruiter with this " + recruiterEmail + "is not found"));
        if (recruiter.getRole()!= Role.RECRUITER){
            throw new IllegalArgumentException("Only recruiter can post jobs");
        }
        Job job = Job.builder()
                .recruiter(recruiter)
                .jobStatus(JobStatus.OPEN)
                .description(jobRequest.description())
                .employmentType(jobRequest.employmentType())
                .title(jobRequest.title())
                .experienceRequired(jobRequest.experienceRequired())
                .location(jobRequest.location())
                .minimumSalary(jobRequest.minSalary())
                .maximumSalary(jobRequest.maxSalary())
                .build();
        jobRepository.save(job);

        return mapToResponse(job);


    }


    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id){
        Job job = jobRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Job with "+id+" id is not found"));
        return mapToResponse(job);
    }



    private JobResponse mapToResponse(Job job){
        return new JobResponse(job.getId(),job.getTitle(),job.getDescription(),
                job.getLocation(), job.getEmploymentType(), job.getMinimumSalary(),
                job.getMaximumSalary(),job.getExperienceRequired(),
                job.getRecruiter().getId(),job.getRecruiter().getName(),job.getJobStatus(),job.getCreatedAt());

    }

    private User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User not found with email"+email));
    }

    private Job getJobEntityById(Long id){
        return jobRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Job was not found with id"+id));
    }


    @Transactional
    public JobResponse updateJob(Long jobId, String recruiterEmail, UpdateJobRequest jobRequest){
        Job job = getJobEntityById(jobId);

        User recruiter = getUserByEmail(recruiterEmail);
        validateJob(job,recruiter);

        job.setTitle(jobRequest.title());
        job.setJobStatus(jobRequest.jobStatus());
        job.setDescription(jobRequest.description());
        job.setLocation(jobRequest.description());
        job.setEmploymentType(jobRequest.employmentType());
        job.setMinimumSalary(jobRequest.minSalary());
        job.setMaximumSalary(jobRequest.maxSalary());
        job.setExperienceRequired(jobRequest.experienceRequired());

        return mapToResponse(jobRepository.save(job));

    }

    private void validateJob(Job job,User recruiter){
        if(!job.getRecruiter().getId().equals(recruiter.getId())){
            throw new UnauthorizedException("You don't have permission to update this job");
        }
    }

    @Transactional
    public void deleteJob(Long jobId,String recruiterEmail){
        User recruiter = getUserByEmail(recruiterEmail);
        Job job = getJobEntityById(jobId);
        validateJob(job,recruiter);
        jobRepository.delete(job);

    }



}
