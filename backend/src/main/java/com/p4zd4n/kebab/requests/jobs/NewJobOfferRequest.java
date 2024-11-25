package com.p4zd4n.kebab.requests.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.entities.JobEmploymentType;
import com.p4zd4n.kebab.entities.JobRequirement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record NewJobOfferRequest(

    @JsonProperty("position_name")
    @Size(min = 1, message = "{name.greaterThanZero}")
    String positionName,

    @Size(min = 1, message = "{description.greaterThanZero}")
    String description,

    @DecimalMin(value = "0.0", inclusive = false, message = "{hourlyWage.greaterThanZero}")
    @NotNull(message = "{hourlyWage.notNull}")
    @JsonProperty("hourly_wage") BigDecimal hourlyWage,

    @JsonProperty("job_employment_types") List<JobEmploymentType> jobEmploymentTypes,

    @JsonProperty("job_requirements") List<JobRequirement> jobRequirements
) {}
