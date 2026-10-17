package com.hirehub.hirehub_api.repository;

import com.hirehub.hirehub_api.entity.Job;
import com.hirehub.hirehub_api.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface JobRepository extends JpaRepository<Job,Long>, JpaSpecificationExecutor<Job> {



    @EntityGraph(attributePaths = {"recruiter","skills"})
    @Query("SELECT j FROM Job j WHERE j.id = :id")
    Optional<Job> findByIdWithDetails(@Param("id") Long id);



    @EntityGraph(attributePaths = {"recruiter","skills"})
    List<Job> findByRecruiterId(Long id);

    @EntityGraph(attributePaths = {"recruiter","skills"})
    List<Job> findByJobStatus(JobStatus status);


    @EntityGraph(attributePaths = {"recruiter","skills"})
    @Override
    Page<Job> findAll(Specification<Job> specs,Pageable pageable);


    @EntityGraph(attributePaths = {"recruiter","skills"})
    List<Job> findByRecruiterIdAndJobStatus(Long id, JobStatus status);
    long countByJobStatus(JobStatus jobStatus);
}
