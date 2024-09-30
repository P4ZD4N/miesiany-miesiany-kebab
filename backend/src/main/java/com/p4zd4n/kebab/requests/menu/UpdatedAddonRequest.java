package com.p4zd4n.kebab.requests.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdatedAddonRequest(

        @JsonProperty("updated_addon_name")
        @Size(min = 1, message = "{name.greaterThanZero}")
        String updatedAddonName,

        @JsonProperty("updated_addon_price")
        @NotNull(message = "{price.notNull}")
        @DecimalMin(value = "0.1", message = "{price.greaterThanZero}")
        BigDecimal updatedAddonPrice
) {}
