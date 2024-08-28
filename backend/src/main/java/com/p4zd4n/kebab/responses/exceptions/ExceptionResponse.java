package com.p4zd4n.kebab.responses.exceptions;

import lombok.Builder;

@Builder
public record ExceptionResponse(
        Integer statusCode,
        String message
) {}
