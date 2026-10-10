package com.hirehub.hirehub_api.service;

import com.hirehub.hirehub_api.dto.common.PageResponse;
import com.hirehub.hirehub_api.dto.job.CreateJobRequest;
import com.hirehub.hirehub_api.dto.job.JobResponse;
import com.hirehub.hirehub_api.dto.job.UpdateJobRequest;
import com.hirehub.hirehub_api.entity.Job;
import com.hirehub.hirehub_api.entity.Skill;
import com.hirehub.hirehub_api.entity.User;
import com.hirehub.hirehub_api.enums.JobStatus;
import com.hirehub.hirehub_api.enums.Role;
import com.hirehub.hirehub_api.exception.ResourceNotFoundException;
import com.hirehub.hirehub_api.exception.UnauthorizedException;
import com.hirehub.hirehub_api.repository.JobRepository;
import com.hirehub.hirehub_api.repository.SkillRepository;
import com.hirehub.hirehub_api.repository.UserRepository;
import com.hirehub.hirehub_api.specifications.JobSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;


    public JobService(UserRepository userRepository,JobRepository jobRepository,SkillRepository skillRepository){
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> searchJobs(
            String keyword,
            String location,
            Integer minExperience,
            Integer maxExperience,
            String employmentType,
            Pageable pageable,
            String skills
    ){
        Specification<Job> specs = JobSpecification
                .filterJobs(keyword,location,minExperience,maxExperience,employmentType,JobStatus.OPEN,skills);
       Page<Job> jobPage = jobRepository.findAll(specs,pageable);
       Page<JobResponse> jobResponsePage = jobPage.map(this::mapToResponse);

       return PageResponse.of(jobResponsePage);
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
        Set<Skill> skills = resolveSkills(jobRequest.skills());

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
                .skills(skills)
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



    private Set<Skill> resolveSkills(Set<String> rawSkills){
        if (rawSkills==null || rawSkills.isEmpty()){
            return new HashSet<>();
        }

        Set<Skill> resolvedSkills = new HashSet<>();

        for (String rawName:rawSkills){
            if (rawName==null || rawName.trim().isEmpty()) continue;
            Skill s = skillRepository.findByNameIgnoreCase(rawName)
                    .orElseGet(()->skillRepository.save(new Skill(rawName)));
            resolvedSkills.add(s);
        }
        return resolvedSkills;

    }




    private JobResponse mapToResponse(Job job){
        Set<String> s = new HashSet<>();
        if (!job.getSkills().isEmpty()) {
         s =job.getSkills()
                    .stream()
                    .map(Skill::getName)
                    .collect(Collectors.toSet());
        }
        return new JobResponse(job.getId(),job.getTitle(),job.getDescription(),
                job.getLocation(), job.getEmploymentType(), job.getMinimumSalary(),
                job.getMaximumSalary(),job.getExperienceRequired(),
                job.getRecruiter().getId(),job.getRecruiter().getName(),job.getJobStatus(),s,job.getCreatedAt());

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
        job.setLocation(jobRequest.location());
        job.setEmploymentType(jobRequest.employmentType());
        job.setMinimumSalary(jobRequest.minSalary());
        job.setMaximumSalary(jobRequest.maxSalary());
        job.setExperienceRequired(jobRequest.experienceRequired());

        Set<String> rawSkills = jobRequest.skills();
        if (rawSkills!=null && !rawSkills.isEmpty()){
            Set<Skill> newSkills = resolveSkills(rawSkills);
            job.setSkills(newSkills);
        }


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
