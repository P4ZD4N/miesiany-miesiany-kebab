package com.p4zd4n.kebab.responses.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ExceptionResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
