package com.p4zd4n.kebab.responses.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record RemovedEmployeeResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
