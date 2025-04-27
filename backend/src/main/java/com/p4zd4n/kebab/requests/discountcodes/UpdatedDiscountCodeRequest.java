package com.p4zd4n.kebab.requests.discountcodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record UpdatedDiscountCodeRequest(

        @NotNull(message = "{discountCode.notNull}")
        String code,

        @JsonProperty("updated_code")
        @Size(min = 1, max = 16, message = "{discountCode.between1And16}")
        String newCode,

        @JsonProperty("updated_discount_percentage")
        @DecimalMin(value = "0.0", message = "{discountPercentage.min}")
        @DecimalMax(value = "100.0", message = "{discountPercentage.max}")
        BigDecimal discountPercentage,

        @JsonProperty("updated_expiration_date")
        @FutureOrPresent(message = "{expirationDate.future}")
        LocalDate expirationDate
) {}
