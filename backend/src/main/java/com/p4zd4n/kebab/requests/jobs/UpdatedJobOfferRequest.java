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
public record UpdatedJobOfferRequest(
    @JsonProperty("position_name") @Size(min = 1, message = "{name.greaterThanZero}")
        String positionName,
    @JsonProperty("updated_position_name")
        @NotBlank(message = "{name.notBlank}")
        @Size(min = 1, max = 25, message = "{name.between1And25}")
        String updatedPositionName,
    @JsonProperty("updated_description")
        @NotBlank(message = "{description.notBlank}")
        @Size(min = 1, max = 250, message = "{description.between1And250}")
        String updatedDescription,
    @DecimalMin(value = "0.0", inclusive = false, message = "{hourlyWage.greaterThanZero}")
        @NotNull(message = "{hourlyWage.notNull}")
        @JsonProperty("updated_hourly_wage")
        BigDecimal updatedHourlyWage,
    @JsonProperty("updated_employment_types") List<JobEmploymentType> updatedEmploymentTypes,
    @JsonProperty("updated_requirements") List<JobRequirement> updatedRequirements,
    @JsonProperty("is_active") Boolean isActive) {}
