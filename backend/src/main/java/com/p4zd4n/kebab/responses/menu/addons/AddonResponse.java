package com.p4zd4n.kebab.responses.menu.addons;

import com.p4zd4n.kebab.entities.AddonPromotion;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddonResponse(
        String name,
        BigDecimal price,
        AddonPromotion promotion
) {}
