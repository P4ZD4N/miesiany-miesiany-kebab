package com.p4zd4n.kebab.requests.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.entities.JobEmploymentType;
import com.p4zd4n.kebab.entities.JobRequirement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record NewJobOfferRequest(
    @JsonProperty("position_name")
        @NotBlank(message = "{name.notBlank}")
        @Size(min = 1, max = 25, message = "{name.between1And25}")
        String positionName,
    @NotBlank(message = "{description.notBlank}")
        @Size(min = 1, max = 250, message = "{description.between1And250}")
        String description,
    @DecimalMin(value = "0.0", inclusive = false, message = "{hourlyWage.greaterThanZero}")
        @NotNull(message = "{hourlyWage.notNull}")
        @JsonProperty("hourly_wage")
        BigDecimal hourlyWage,
    @JsonProperty("job_employment_types") List<JobEmploymentType> jobEmploymentTypes,
    @JsonProperty("job_requirements") List<JobRequirement> jobRequirements) {}
