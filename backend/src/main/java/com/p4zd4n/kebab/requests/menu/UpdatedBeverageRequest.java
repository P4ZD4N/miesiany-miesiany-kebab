package com.p4zd4n.kebab.requests.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdatedBeverageRequest(

        @Size(min = 1, message = "{name.greaterThanZero}")
        String name,

        @NotNull(message = "{price.notNull}")
        @DecimalMin(value = "0.1", message = "{price.greaterThanZero}")
        BigDecimal price,

        @JsonProperty("new_capacity")
        @NotNull(message = "{capacity.notNull}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{capacity.greaterThanZero}")
        BigDecimal newCapacity,

        @JsonProperty("old_capacity")
        @NotNull(message = "{capacity.notNull}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{capacity.greaterThanZero}")
        BigDecimal oldCapacity
) {}
