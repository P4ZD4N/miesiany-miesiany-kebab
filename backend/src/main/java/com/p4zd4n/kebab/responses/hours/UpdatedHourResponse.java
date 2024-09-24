package com.p4zd4n.kebab.responses.hours;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UpdatedHourResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
