package com.hirehub.hirehub_api.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "skills")
@Builder
@Entity
@EqualsAndHashCode(of = "name")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,length = 50)
    String name;

    public Skill(String name){
        this.name = name;
    }




}
