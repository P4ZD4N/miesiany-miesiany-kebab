package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class DiscountCodeNotFoundException extends RuntimeException {

  private final String code;

  public DiscountCodeNotFoundException(String code) {
    super("Discount code '" + code + "' not found!");
    this.code = code;
  }
}
