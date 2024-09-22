package com.p4zd4n.kebab.requests.menu;

import java.math.BigDecimal;

public record UpdatedBeverageRequest(
        String name,
        BigDecimal price,
        BigDecimal capacity
) {}
