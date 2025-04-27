package com.p4zd4n.kebab.responses.discountcodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record DiscountCodeResponse(
        Long id,
        String code,
        @JsonProperty("discount_percentage") BigDecimal discountPercentage,
        @JsonProperty("expiration_date") LocalDate expirationDate
) {}
