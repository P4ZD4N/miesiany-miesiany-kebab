package com.p4zd4n.kebab.requests.menu.beverages;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record UpdatedBeverageRequest(
    @JsonProperty("updated_beverage_name")
        @NotBlank(message = "{name.notBlank}")
        @Size(min = 1, max = 25, message = "{name.between1And25}")
        String updatedBeverageName,
    @JsonProperty("updated_beverage_price")
        @NotNull(message = "{price.notNull}")
        @DecimalMin(value = "0.1", message = "{price.greaterThanZero}")
        BigDecimal updatedBeveragePrice,
    @JsonProperty("updated_beverage_new_capacity")
        @NotNull(message = "{capacity.notNull}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{capacity.greaterThanZero}")
        BigDecimal updatedBeverageNewCapacity,
    @JsonProperty("updated_beverage_old_capacity")
        @NotNull(message = "{capacity.notNull}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{capacity.greaterThanZero}")
        BigDecimal updatedBeverageOldCapacity) {}
