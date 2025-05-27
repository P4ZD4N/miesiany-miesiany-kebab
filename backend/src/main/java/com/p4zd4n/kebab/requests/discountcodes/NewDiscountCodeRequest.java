package com.p4zd4n.kebab.requests.discountcodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record NewDiscountCodeRequest(

        @Size(min = 1, max = 16, message = "{discountCode.between1And16}")
        String code,

        @JsonProperty("discount_percentage")
        @NotNull(message = "{discountPercentage.notNull}")
        @DecimalMin(value = "0.0", message = "{discountPercentage.min}")
        @DecimalMax(value = "100.0", message = "{discountPercentage.max}")
        BigDecimal discountPercentage,

        @JsonProperty("expiration_date")
        @NotNull(message = "{expirationDate.notNull}")
        @FutureOrPresent(message = "{expirationDate.future}")
        LocalDate expirationDate,

        @JsonProperty("remaining_uses")
        @NotNull(message = "{remainingUses.notNull}")
        @Min(value = 1, message = "{remainingUses.greaterThan1}")
        Long remainingUses
) {}
