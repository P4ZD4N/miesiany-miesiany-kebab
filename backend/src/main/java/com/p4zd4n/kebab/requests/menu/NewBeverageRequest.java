package com.p4zd4n.kebab.requests.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record NewBeverageRequest(

        @JsonProperty("new_beverage_name")
        @Size(min = 1, message = "{name.greaterThanZero}")
        String newBeverageName,

        @JsonProperty("new_beverage_capacity")
        @NotNull(message = "{capacity.notNull}")
        @DecimalMin(value = "0.1", message = "{capacity.greaterThanZero}")
        BigDecimal newBeverageCapacity,

        @JsonProperty("new_beverage_price")
        @NotNull(message = "{price.notNull}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{price.greaterThanZero}")
        BigDecimal newBeveragePrice
) {}
