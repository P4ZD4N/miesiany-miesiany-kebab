package com.p4zd4n.kebab.requests.promotions.beveragepromotions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record NewBeveragePromotionRequest(

        @Size(min = 1, max = 100, message = "{description.between1And100}")
        String description,

        @JsonProperty("discount_percentage")
        @NotNull(message = "{discountPercentage.notNull}")
        @DecimalMin(value = "0.0", message = "{discountPercentage.min}")
        @DecimalMax(value = "100.0", message = "{discountPercentage.max}")
        BigDecimal discountPercentage,

        @JsonProperty("beverages_with_capacities")
        Map<String, List<BigDecimal>> beveragesWithCapacities
) {}
