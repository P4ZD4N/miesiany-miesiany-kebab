package com.p4zd4n.kebab.responses.menu;

import com.p4zd4n.kebab.enums.IngredientType;
import lombok.Builder;

@Builder
public record IngredientResponse(
    String name,
    IngredientType ingredientType
) {}
