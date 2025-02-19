package com.p4zd4n.kebab.requests.promotions.beveragepromotions;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RemovedBeveragePromotionRequest(

        @NotNull(message = "{id.notNull}")
        Long id
) {}
