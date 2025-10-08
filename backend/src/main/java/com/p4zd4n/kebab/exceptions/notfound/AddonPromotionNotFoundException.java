package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class AddonPromotionNotFoundException extends RuntimeException {

  private final Long id;

  public AddonPromotionNotFoundException(Long id) {
    super("Addon promotion with id '" + id + "' not found!");
    this.id = id;
  }
}
