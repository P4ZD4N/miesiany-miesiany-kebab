package com.p4zd4n.kebab.entities.key;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.p4zd4n.kebab.entities.Ingredient;
import com.p4zd4n.kebab.enums.IngredientType;
import com.p4zd4n.kebab.exceptions.invalid.InvalidIngredientException;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MealKey {

    private final String mealName;
    private final Ingredient meat;
    private final Ingredient sauce;

    @Builder
    public MealKey(String mealName, Ingredient meat, Ingredient sauce) {

        if (meat.getIngredientType() != IngredientType.MEAT) {
            throw new InvalidIngredientException(meat.getIngredientType(), IngredientType.MEAT);
        }

        if (sauce.getIngredientType() != IngredientType.SAUCE) {
            throw new InvalidIngredientException(sauce.getIngredientType(), IngredientType.SAUCE);
        }

        this.mealName = mealName;
        this.meat = meat;
        this.sauce = sauce;
    }

    @JsonIgnore
    public String toStringKey() {
        return mealName + "_" + meat.getName() + "_" + sauce.getName();
    }
}

