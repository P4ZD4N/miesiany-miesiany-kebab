package com.p4zd4n.kebab.responses.promotions.beveragepromotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record NewBeveragePromotionResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
