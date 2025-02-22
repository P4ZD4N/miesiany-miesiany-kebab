package com.p4zd4n.kebab.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_offers")
@Getter
@Setter
@NoArgsConstructor
public class JobOffer extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "position_name", nullable = false, unique = true)
    private String positionName;

    @Column(name = "description")
    private String description;

    @Column(name = "hourly_wage", nullable = false)
    private BigDecimal hourlyWage;

    @Column(name = "is_active")
    private boolean isActive = true;

    @OneToMany(mappedBy = "jobOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobEmploymentType> jobEmploymentTypes = new ArrayList<>();

    @OneToMany(mappedBy = "jobOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobRequirement> jobRequirements = new ArrayList<>();

    @OneToMany(mappedBy = "jobOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> jobApplications = new ArrayList<>();

    @Builder
    public JobOffer(String positionName, String description, BigDecimal hourlyWage) {
        this.positionName = positionName;
        this.description = description;
        this.hourlyWage = hourlyWage;
    }

    @Builder
    public JobOffer(String positionName, String description, BigDecimal hourlyWage, List<JobEmploymentType> jobEmploymentTypes, List<JobRequirement> jobRequirements) {
        this.positionName = positionName;
        this.description = description;
        this.hourlyWage = hourlyWage;
        this.jobEmploymentTypes = jobEmploymentTypes;
        this.jobRequirements = jobRequirements;
    }

    public void addRequirement(JobRequirement requirement) {
        requirement.setJobOffer(this);
        jobRequirements.add(requirement);
    }

    public void removeRequirement(JobRequirement requirement) {
        jobRequirements.remove(requirement);
    }

    public void addEmploymentType(JobEmploymentType jobEmploymentType) {
        jobEmploymentType.setJobOffer(this);
        jobEmploymentTypes.add(jobEmploymentType);
    }

    public void removeEmploymentType(JobEmploymentType jobEmploymentType) {
        jobEmploymentTypes.remove(jobEmploymentType);
    }
}
