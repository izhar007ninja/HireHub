package com.hirehub.hirehub_api.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recruiter_profiles")
@Builder
@Getter
@Setter
@Entity
public class RecruiterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_description",columnDefinition = "TEXT")
    private String companyDescription;

    @Column(name = "company_website")
    private String companyWebsite;

    private String location;



}
