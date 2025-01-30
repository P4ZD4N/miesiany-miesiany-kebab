package com.p4zd4n.kebab.requests.promotions.mealpromotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Builder
public record UpdatedMealPromotionRequest(

        @JsonProperty("updated_meal_promotion_id")
        Long updatedMealPromotionId,

        @JsonProperty("updated_meal_promotion_description")
        @Size(min = 1, max = 100, message = "{description.between1And100}")
        String updatedMealPromotionDescription,

        @JsonProperty("updated_meal_promotion_sizes")
        Set<com.p4zd4n.kebab.enums.Size> updatedMealPromotionSizes,

        @JsonProperty("updated_meal_promotion_discount_percentage")
        BigDecimal updatedMealPromotionDiscountPercentage,

        @JsonProperty("updated_meal_promotion_meal_names") List<String> updatedMealPromotionMealNames
) {}
