package com.p4zd4n.kebab.responses.menu;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BeverageResponse(
        String name,
        BigDecimal capacity,
        BigDecimal price
) {}