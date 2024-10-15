package com.p4zd4n.kebab.responses.contact;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UpdatedContactResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
