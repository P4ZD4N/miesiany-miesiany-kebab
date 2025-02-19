package com.p4zd4n.kebab.responses.promotions.addonpromotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record AddonPromotionResponse(
        Long id,
        String description,
        @JsonProperty("discount_percentage") BigDecimal discountPercentage,
        @JsonProperty("addon_names") Set<String> addonNames
) {}
