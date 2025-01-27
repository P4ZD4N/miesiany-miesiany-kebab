package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class MealPromotionNotFoundException extends RuntimeException {

    private final Long id;

    public MealPromotionNotFoundException(Long id) {
        super("Meal promotion with id '" + id + "' not found!");
        this.id = id;
    }
}
