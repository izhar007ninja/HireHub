package com.hirehub.hirehub_api.repository;

import com.hirehub.hirehub_api.entity.Job;
import com.hirehub.hirehub_api.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface JobRepository extends JpaRepository<Job,Long> {

    List<Job> findByRecruiterId(Long id);
    List<Job> findByJobStatus(JobStatus status);
}
