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
public record UpdatedJobOfferRequest(

        @JsonProperty("position_name")
        @Size(min = 1, message = "{name.greaterThanZero}")
        String positionName,

        @JsonProperty("updated_position_name")
        @Size(min = 1, message = "{name.greaterThanZero}")
        String updatedPositionName,

        @JsonProperty("updated_description")
        @Size(min = 1, message = "{description.greaterThanZero}")
        String updatedDescription,

        @DecimalMin(value = "0.0", inclusive = false, message = "{hourlyWage.greaterThanZero}")
        @NotNull(message = "{hourlyWage.notNull}")
        @JsonProperty("updated_hourly_wage") BigDecimal updatedHourlyWage,

        @JsonProperty("updated_employment_types") List<JobEmploymentType> updatedEmploymentTypes,

        @JsonProperty("updated_requirements") List<JobRequirement> updatedRequirements
) {}
