package com.p4zd4n.kebab.responses.promotions.beveragepromotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Builder
public record BeveragePromotionResponse(
        Long id,
        String description,
        @JsonProperty("discount_percentage") BigDecimal discountPercentage,
        @JsonProperty("beverages_with_capacities") Map<String, List<BigDecimal>> beveragesWithCapacities
) {}
