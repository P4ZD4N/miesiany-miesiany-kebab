package com.p4zd4n.kebab.responses.menu.ingredients;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record NewIngredientResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
