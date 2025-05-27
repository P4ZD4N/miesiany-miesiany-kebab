package com.p4zd4n.kebab.requests.orders;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RemovedOrderRequest(

        @NotNull(message = "{id.notNull}")
        Long id
) {}
