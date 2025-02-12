package com.p4zd4n.kebab.requests.promotions.addonpromotions;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RemovedAddonPromotionRequest(

        @NotNull(message = "{id.notNull}")
        Long id
) {}
