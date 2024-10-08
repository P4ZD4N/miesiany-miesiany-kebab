package com.p4zd4n.kebab.requests.menu.ingredients;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RemovedIngredientRequest(

        @Size(min = 1, message = "{name.greaterThanZero}")
        String name
) {}
