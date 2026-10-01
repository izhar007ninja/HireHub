package com.hirehub.hirehub_api.entity;


import com.hirehub.hirehub_api.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jobs")
@Builder
@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id",nullable = false)
    private User recruiter;

    @Column(nullable = false,length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;


    @Column(nullable = false)
    private String location;

    @Column(nullable = false,name = "employment_type",length =150)
    private String employmentType;

    @Column(name = "experience_required")
    private Integer experienceRequired;

    @Column(name = "min_salary",precision = 12,scale = 2)
    private BigDecimal minimumSalary;

    @Column(name = "max_salary",precision = 12,scale = 2)
    private BigDecimal maximumSalary;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private JobStatus jobStatus;


    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name = "job_skills",joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skills_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();


}
