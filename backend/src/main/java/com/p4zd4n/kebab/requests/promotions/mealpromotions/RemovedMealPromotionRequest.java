package com.p4zd4n.kebab.requests.promotions.mealpromotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RemovedMealPromotionRequest(

        @JsonProperty("id")
        @NotNull(message = "{id.notNull}")
        Long id
) {}
