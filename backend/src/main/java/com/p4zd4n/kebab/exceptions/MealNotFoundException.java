package com.p4zd4n.kebab.exceptions;

import lombok.Getter;

@Getter
public class MealNotFoundException extends RuntimeException {

    private final String mealName;

    public MealNotFoundException(String name) {
        super("Meal with name '" + name + "' not found!");
        mealName = name;
    }
}
