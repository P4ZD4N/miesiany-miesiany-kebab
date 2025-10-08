package com.p4zd4n.kebab.exceptions.alreadyexists;

import lombok.Getter;

@Getter
public class IngredientAlreadyExistsException extends RuntimeException {

  private final String ingredientName;

  public IngredientAlreadyExistsException(String name) {
    super("Ingredient with name '" + name + "' already exists!");
    ingredientName = name;
  }
}
