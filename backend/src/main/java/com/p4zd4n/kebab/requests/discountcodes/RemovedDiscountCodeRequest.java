package com.p4zd4n.kebab.requests.discountcodes;

import jakarta.validation.constraints.NotNull;

public record RemovedDiscountCodeRequest(

        @NotNull(message = "{discountCode.notNull}")
        String code
) {}
