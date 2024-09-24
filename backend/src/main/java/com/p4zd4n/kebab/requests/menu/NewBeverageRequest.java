package com.p4zd4n.kebab.requests.menu;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record NewBeverageRequest(

        @Size(min = 1, message = "{name.greaterThanZero}")
        String name,

        @NotNull(message = "{capacity.notNull}")
        @DecimalMin(value = "0.1", message = "{capacity.greaterThanZero}")
        BigDecimal capacity,

        @NotNull(message = "{price.notNull}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{price.greaterThanZero}")
        BigDecimal price
) {}