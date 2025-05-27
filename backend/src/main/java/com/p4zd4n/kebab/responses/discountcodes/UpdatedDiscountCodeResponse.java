package com.p4zd4n.kebab.responses.discountcodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UpdatedDiscountCodeResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
