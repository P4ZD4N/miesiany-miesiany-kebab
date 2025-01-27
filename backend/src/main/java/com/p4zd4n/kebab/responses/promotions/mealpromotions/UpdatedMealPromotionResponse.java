package com.p4zd4n.kebab.responses.promotions.mealpromotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UpdatedMealPromotionResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
