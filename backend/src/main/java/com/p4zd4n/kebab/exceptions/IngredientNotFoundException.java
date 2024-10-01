package com.p4zd4n.kebab.exceptions;

import lombok.Getter;

@Getter
public class IngredientNotFoundException extends RuntimeException {

    private final String ingredientName;

    public IngredientNotFoundException(String name) {
        super("Ingredient with name '" + name + "' not found!");
        ingredientName = name;
    }
}
