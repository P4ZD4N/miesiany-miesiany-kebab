package com.p4zd4n.kebab.exceptions.alreadyexists;

import lombok.Getter;

@Getter
public class DiscountCodeAlreadyExistsException extends RuntimeException {

  private final String code;

  public DiscountCodeAlreadyExistsException(String code) {
    super("Discount code '" + code + "' already exists!");
    this.code = code;
  }
}
