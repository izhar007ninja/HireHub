package com.hirehub.hirehub_api.repository;


import com.hirehub.hirehub_api.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ApplicationRepository extends JpaRepository<Application,Long> {
    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);

    List<Application> findByCandidateIdOrderByAppliedAtDesc(Long candidateId);
    List<Application> findByJobIdOrderByAppliedAtDesc(Long jobId);
    Optional<Application> findByIdAndCandidateId(Long id,Long candidateId);


}
