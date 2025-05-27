package com.p4zd4n.kebab.exceptions.invalid;

import lombok.Getter;

@Getter
public class InvalidMealKeyFormatException extends RuntimeException {

    private final String mealKey;

    public InvalidMealKeyFormatException(String mealKey) {
        super("Invalid meal key format: " + mealKey);
        this.mealKey = mealKey;
    }
}
