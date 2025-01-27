package com.p4zd4n.kebab.requests.promotions.mealpromotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Builder
public record NewMealPromotionRequest(

        @JsonProperty("new_meal_promotion_description")
        @Size(min = 1, max = 100, message = "{description.between1And100}")
        String newMealPromotionDescription,

        @JsonProperty("new_meal_promotion_sizes")
        Set<com.p4zd4n.kebab.enums.Size> newMealPromotionSizes,

        @JsonProperty("new_meal_promotion_discount_percentage")
        BigDecimal newMealPromotionDiscountPercentage,

        @JsonProperty("new_meal_promotion_meal_names") List<String> newMealPromotionMealNames
) {}