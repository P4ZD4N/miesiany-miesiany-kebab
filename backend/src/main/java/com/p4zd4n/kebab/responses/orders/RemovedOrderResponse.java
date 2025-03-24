package com.p4zd4n.kebab.responses.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record RemovedOrderResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
