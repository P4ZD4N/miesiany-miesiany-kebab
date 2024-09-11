package com.p4zd4n.kebab.responses.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.Role;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message,
        Role role
) {}
