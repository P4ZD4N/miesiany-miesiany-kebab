package com.p4zd4n.kebab.responses.menu.addons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UpdatedAddonResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
