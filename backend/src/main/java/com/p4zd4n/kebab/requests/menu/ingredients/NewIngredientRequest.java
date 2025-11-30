package com.p4zd4n.kebab.requests.menu.ingredients;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.IngredientType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record NewIngredientRequest(
    @JsonProperty("new_ingredient_name")
        @NotBlank(message = "{name.notBlank}")
        @Size(min = 1, max = 25, message = "{name.between1And25}")
        String newIngredientName,
    @JsonProperty("new_ingredient_type") @NotNull(message = "{ingredientType.notNull}")
        IngredientType newIngredientType) {}
