package com.p4zd4n.kebab.responses.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record NewAddonResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
