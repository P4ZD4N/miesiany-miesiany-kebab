package com.p4zd4n.kebab.requests.menu.meals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.responses.menu.meals.SimpleMealIngredient;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import lombok.Builder;

@Builder
public record NewMealRequest(
    @JsonProperty("new_meal_name") @Size(min = 1, message = "{name.greaterThanZero}")
        String newMealName,
    @JsonProperty("new_meal_prices")
        @NotNull(message = "{prices.notNull}")
        @Size(min = 1, message = "{prices.notEmpty}")
        EnumMap<
                com.p4zd4n.kebab.enums.Size,
                @DecimalMin(value = "0.0", inclusive = false, message = "{price.greaterThanZero}")
                BigDecimal>
            prices,
    @JsonProperty("new_meal_ingredients") @NotEmpty(message = "{ingredients.notEmpty}")
        List<SimpleMealIngredient> ingredients) {}
