package com.p4zd4n.kebab.responses.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record LogoutResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
