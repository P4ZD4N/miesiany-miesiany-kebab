package com.p4zd4n.kebab.responses.promotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Builder
public record MealPromotionResponse(
        String description,
        Set<Size> sizes,
        @JsonProperty("discount_percentage") BigDecimal discountPercentage,
        @JsonProperty("meal_names") List<String> mealNames
) {}
