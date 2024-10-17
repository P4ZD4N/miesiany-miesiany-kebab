package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.JobRequirementType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "job_requirements")
@Getter
@Setter
@NoArgsConstructor
public class JobRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "requirement_type", nullable = false)
    private JobRequirementType requirementType;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "job_offer_id")
    private JobOffer jobOffer;

    @Builder
    public JobRequirement(JobRequirementType requirementType, String description) {
        this.requirementType = requirementType;
        this.description = description;
    }
}
