package com.p4zd4n.kebab.requests.promotions.beveragepromotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Builder
public record UpdatedBeveragePromotionRequest(

        @NotNull(message = "{id.notNull}")
        Long id,

        @JsonProperty("updated_description")
        @Size(min = 1, max = 100, message = "{description.between1And100}")
        String updatedDescription,

        @JsonProperty("updated_discount_percentage")
        BigDecimal updatedDiscountPercentage,

        @JsonProperty("updated_beverages_with_capacities")
        Map<String, List<BigDecimal>> updatedBeveragesWithCapacities
) {}
