package com.p4zd4n.kebab.requests.menu.addons;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record NewAddonRequest(
    @JsonProperty("new_addon_name") @Size(min = 1, message = "{name.greaterThanZero}")
        String newAddonName,
    @JsonProperty("new_addon_price")
        @NotNull(message = "{price.notNull}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{price.greaterThanZero}")
        BigDecimal newAddonPrice) {}
