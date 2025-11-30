package com.p4zd4n.kebab.requests.menu.meals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.responses.menu.meals.SimpleMealIngredient;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdatedMealRequest(
    @JsonProperty("updated_meal_name")
        @NotBlank(message = "{name.notBlank}")
        @Size(min = 1, max = 25, message = "{name.between1And25}")
        String updatedMealName,
    @JsonProperty("updated_meal_prices")
        @NotNull(message = "{prices.notNull}")
        @Size(min = 1, message = "{prices.notEmpty}")
        EnumMap<
                com.p4zd4n.kebab.enums.Size,
                @DecimalMin(value = "0.0", inclusive = false, message = "{price.greaterThanZero}")
                BigDecimal>
            updatedPrices,
    @JsonProperty("updated_meal_ingredients") @NotEmpty(message = "{ingredients.notEmpty}")
        List<SimpleMealIngredient> updatedIngredients) {}
