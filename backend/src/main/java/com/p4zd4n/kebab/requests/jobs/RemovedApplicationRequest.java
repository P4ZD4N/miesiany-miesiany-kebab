package com.p4zd4n.kebab.requests.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RemovedApplicationRequest(

        @JsonProperty("position_name")
        @Size(min = 1, message = "{name.greaterThanZero}")
        String positionName,

        @JsonProperty("application_id")
        @NotNull(message = "{id.notNull}")
        Long applicationId
) {}
