package com.p4zd4n.kebab.exceptions.invalid;

import com.p4zd4n.kebab.enums.IngredientType;
import lombok.Getter;

@Getter
public class InvalidIngredientException extends RuntimeException {

    private final IngredientType invalidIngredientType;
    private final IngredientType validIngredientType;

    public InvalidIngredientException(IngredientType invalidIngredientType, IngredientType validIngredientType) {
        super("Invalid ingredient type: '" + invalidIngredientType + "' where valid ingredient type is: '" + validIngredientType + "'");
        this.invalidIngredientType = invalidIngredientType;
        this.validIngredientType = validIngredientType;
    }
}
