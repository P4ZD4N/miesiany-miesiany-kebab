package com.p4zd4n.kebab.exceptions.alreadyexists;

import lombok.Getter;

@Getter
public class MealPromotionAlreadyExists extends RuntimeException {

  private final String mealName;

  public MealPromotionAlreadyExists(String mealName) {
    super("Meal promotion for '" + mealName + "' with the same sizes already exists!");
    this.mealName = mealName;
  }
}
