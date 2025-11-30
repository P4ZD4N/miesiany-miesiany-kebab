package com.p4zd4n.kebab.requests.menu.addons;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record UpdatedAddonRequest(
    @JsonProperty("updated_addon_name")
        @NotBlank(message = "{name.notBlank}")
        @Size(min = 1, max = 25, message = "{name.between1And25}")
        String updatedAddonName,
    @JsonProperty("updated_addon_price")
        @NotNull(message = "{price.notNull}")
        @DecimalMin(value = "0.1", message = "{price.greaterThanZero}")
        BigDecimal updatedAddonPrice) {}
