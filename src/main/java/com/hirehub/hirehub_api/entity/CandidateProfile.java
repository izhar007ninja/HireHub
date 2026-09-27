package com.hirehub.hirehub_api.entity;


import jakarta.persistence.*;
import lombok.*;


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

    private String phone;

    private String location;


    @Column(columnDefinition = "TEXT")
    private String bio;

    private Integer experienceYears;
    private String resumeUrl;



}
