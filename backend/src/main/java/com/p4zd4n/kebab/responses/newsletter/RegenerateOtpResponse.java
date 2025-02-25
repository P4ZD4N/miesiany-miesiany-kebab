package com.p4zd4n.kebab.responses.newsletter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record RegenerateOtpResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
