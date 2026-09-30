package com.hirehub.hirehub_api.entity;


import com.hirehub.hirehub_api.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true,length = 150)
    private String email;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private Role role;

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;


    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    public User(String name, String password, String email, Role role,boolean enabled) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.enabled = enabled;


    }
}
