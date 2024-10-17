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
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "position_name", nullable = false, unique = true)
    private String positionName;

    @Column(name = "description")
    private String description;

    @Column(name = "monthly_salary", nullable = false)
    private BigDecimal monthlySalary;

    @OneToMany(mappedBy = "jobOffer")
    private List<JobRequirement> jobRequirements = new ArrayList<>();

    @OneToMany(mappedBy = "jobOffer")
    private List<JobApplication> jobApplications = new ArrayList<>();

    @Builder
    public JobOffer(String positionName, String description, BigDecimal monthlySalary) {
        this.positionName = positionName;
        this.description = description;
        this.monthlySalary = monthlySalary;
    }

    @Builder
    public JobOffer(String positionName, String description, BigDecimal monthlySalary, List<JobRequirement> jobRequirements) {
        this.positionName = positionName;
        this.description = description;
        this.monthlySalary = monthlySalary;
        this.jobRequirements = jobRequirements;
    }

    public void addRequirement(JobRequirement requirement) {
        jobRequirements.add(requirement);
    }

    public void removeRequirement(JobRequirement requirement) {
        jobRequirements.remove(requirement);
    }
}
