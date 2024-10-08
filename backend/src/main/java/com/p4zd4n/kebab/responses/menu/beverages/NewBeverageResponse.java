package com.p4zd4n.kebab.responses.menu.beverages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record NewBeverageResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
