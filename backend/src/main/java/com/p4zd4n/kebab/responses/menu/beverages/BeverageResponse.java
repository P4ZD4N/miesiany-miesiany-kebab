package com.p4zd4n.kebab.responses.menu.beverages;

import com.p4zd4n.kebab.entities.BeveragePromotion;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BeverageResponse(
        String name,
        BigDecimal capacity,
        BigDecimal price,
        BeveragePromotion promotion
) {}
