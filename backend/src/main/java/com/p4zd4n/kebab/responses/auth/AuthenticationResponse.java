package com.p4zd4n.kebab.responses.auth;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        Integer statusCode,
        String message
) {}
