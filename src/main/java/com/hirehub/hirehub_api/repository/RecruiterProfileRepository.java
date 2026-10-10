package com.hirehub.hirehub_api.repository;

import com.hirehub.hirehub_api.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile,Long> {

    Optional<RecruiterProfile> findByUserId(Long id);

    @Query("""
           SELECT rp
           FROM RecruiterProfile rp
           WHERE rp.companyName IS NOT NULL
           AND TRIM(rp.companyName) != ''
           """)
    List<RecruiterProfile> findAllWithCompanies();
}
