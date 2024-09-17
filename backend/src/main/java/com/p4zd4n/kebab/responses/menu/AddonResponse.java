package com.p4zd4n.kebab.responses.menu;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddonResponse(
        String name,
        BigDecimal price
) {}
