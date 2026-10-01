package com.hirehub.hirehub_api.specifications;

import com.hirehub.hirehub_api.entity.Job;
import com.hirehub.hirehub_api.entity.Skill;
import com.hirehub.hirehub_api.enums.JobStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    public static Specification<Job> filterJobs(
            String keyword,
            String location,
            Integer minExperience,
            Integer maxExperience,
            String employmentType,
            JobStatus jobStatus,
            String skill
    ){
        return (root, query, criteriaBuilder) ->{

            List<Predicate> predicates = new ArrayList<>();

            if(jobStatus!=null){
                predicates.add(criteriaBuilder.equal(root.get("jobStatus"),jobStatus));
            }
            if (keyword!=null && !keyword.trim().isEmpty()){
                String pattern = "%"+keyword.trim().toLowerCase()+"%";
                Predicate titleMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),pattern);
                Predicate desMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),pattern);
                predicates.add(criteriaBuilder.or(titleMatch,desMatch));
            }

            if (location!=null && !location.trim().isEmpty()){
                String locationPattern = "%"+location.trim().toLowerCase()+"%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")),locationPattern));
            }
            if (minExperience!=null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("experienceRequired"),minExperience));
            }

            if (maxExperience!=null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("experienceRequired"),maxExperience));
            }

            if (employmentType!=null && !employmentType.trim().isEmpty()){
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("employmentType")),employmentType.trim().toLowerCase()));
            }

            if (skill!=null && !skill.trim().isEmpty()){
                Join<Job, Skill> jobSkillJoin = root.join("skills");
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(jobSkillJoin.get("name")),skill.trim().toLowerCase()));
                query.distinct(true);
            }




            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
