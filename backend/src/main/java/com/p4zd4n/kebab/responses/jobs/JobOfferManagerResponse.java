package com.p4zd4n.kebab.responses.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.entities.JobApplication;
import com.p4zd4n.kebab.entities.JobRequirement;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record JobOfferManagerResponse(
        @JsonProperty("position_name") String positionName,
        String description,
        @JsonProperty("monthly_salary") BigDecimal monthlySalary,
        @JsonProperty("job_requirements") List<JobRequirement> jobRequirements,
        @JsonProperty("job_applications") List<JobApplication> jobApplications
) {}
