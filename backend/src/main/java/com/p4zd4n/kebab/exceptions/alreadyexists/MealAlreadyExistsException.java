package com.p4zd4n.kebab.exceptions.alreadyexists;

import lombok.Getter;

@Getter
public class MealAlreadyExistsException extends RuntimeException {

    private final String mealName;

    public MealAlreadyExistsException(String name) {
        super("Meal with name '" + name + "' already exists!");
        mealName = name;
    }
}
