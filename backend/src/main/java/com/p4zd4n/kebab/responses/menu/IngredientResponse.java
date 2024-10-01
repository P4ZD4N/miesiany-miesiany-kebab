package com.p4zd4n.kebab.responses.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.IngredientType;
import lombok.Builder;

@Builder
public record IngredientResponse(
    String name,
    @JsonProperty("ingredient_type") IngredientType ingredientType
) {}
