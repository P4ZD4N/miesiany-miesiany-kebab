package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class BeveragePromotionNotFoundException extends RuntimeException {

  private final Long id;

  public BeveragePromotionNotFoundException(Long id) {
    super("Beverage promotion with id '" + id + "' not found!");
    this.id = id;
  }
}
