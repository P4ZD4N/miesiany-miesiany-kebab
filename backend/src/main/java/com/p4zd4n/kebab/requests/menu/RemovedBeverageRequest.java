package com.p4zd4n.kebab.requests.menu;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RemovedBeverageRequest(

        @Size(min = 1, message = "{name.greaterThanZero}")
        String name
) {}
