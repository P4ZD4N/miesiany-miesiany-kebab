package com.p4zd4n.kebab.responses.menu.meals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.enums.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;

@Builder
public record MealResponse(
        @JsonProperty("name") String name,
        EnumMap<Size, BigDecimal> prices,
        @JsonProperty("ingredients") List<SimpleMealIngredient> ingredients,
        @JsonProperty("meal_promotions") List<MealPromotion> mealPromotions
) {}
