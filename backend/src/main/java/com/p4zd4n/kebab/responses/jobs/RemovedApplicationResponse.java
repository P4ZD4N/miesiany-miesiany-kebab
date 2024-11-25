package com.p4zd4n.kebab.responses.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record RemovedApplicationResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
