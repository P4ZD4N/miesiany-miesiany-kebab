package com.p4zd4n.kebab.requests.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.responses.menu.SimpleMealIngredient;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;

@Builder
public record UpdatedMealRequest(

        @JsonProperty("updated_meal_name")
        @Size(min = 1, message = "{name.greaterThanZero}")
        String updatedMealName,

        @JsonProperty("updated_meal_prices")
        @NotNull(message = "{prices.notNull}")
        @Size(min = 1, message = "{prices.notEmpty}")
        EnumMap<com.p4zd4n.kebab.enums.Size, @DecimalMin(value = "0.0", inclusive = false, message = "{price.greaterThanZero}") BigDecimal> updatedPrices,

        @JsonProperty("updated_meal_ingredients")
        @NotEmpty(message = "{ingredients.notEmpty}")
        List<SimpleMealIngredient> updatedIngredients
) {}
