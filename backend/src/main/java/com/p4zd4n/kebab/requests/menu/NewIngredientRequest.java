package com.p4zd4n.kebab.requests.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.IngredientType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record NewIngredientRequest(

        @JsonProperty("new_ingredient_name")
        @Size(min = 1, message = "{name.greaterThanZero}")
        String newIngredientName,

        @JsonProperty("new_ingredient_type")
        @NotNull(message = "{ingredientType.notNull}")
        IngredientType newIngredientType
) {}
