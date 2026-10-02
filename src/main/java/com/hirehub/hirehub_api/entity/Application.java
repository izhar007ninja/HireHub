package com.hirehub.hirehub_api.entity;

import com.hirehub.hirehub_api.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application",
uniqueConstraints = @UniqueConstraint(name = "uk_job_candidate",columnNames = {"job_id","candidate_id"})
)
@Getter
@Setter
@Builder
@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id",nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id",nullable = false)
    private User candidate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    @Builder.Default
    private ApplicationStatus applicationStatus = ApplicationStatus.APPLIED;


    @Column(name = "cover_letter",columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "resume_url")
    private String resumeUrl;

    @CreationTimestamp
    @Column(name = "applied_at",nullable = false,updatable = false)
    private LocalDateTime appliedAt;

    @UpdateTimestamp
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updateAt;


}
