package com.p4zd4n.kebab.requests.menu.meals;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RemovedMealRequest(

        @Size(min = 1, message = "{name.greaterThanZero}")
        String name
) {}
