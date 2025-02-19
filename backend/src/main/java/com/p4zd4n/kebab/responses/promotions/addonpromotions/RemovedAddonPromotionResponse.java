package com.p4zd4n.kebab.responses.promotions.addonpromotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record RemovedAddonPromotionResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
