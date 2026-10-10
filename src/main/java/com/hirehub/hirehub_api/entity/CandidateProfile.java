package com.hirehub.hirehub_api.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "candidate_profiles")
@Entity
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,unique = true, name = "user_id")
    private User user;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String location;


    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;



    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "candidate_skills",
    joinColumns = @JoinColumn(name = "candidate_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "skill id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();




}
