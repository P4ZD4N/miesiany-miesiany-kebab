package com.p4zd4n.kebab.exceptions.others;

public class ManagerPromotionNotAllowedException extends RuntimeException {

  public ManagerPromotionNotAllowedException() {
    super("Changing position to manager is not allowed!");
  }
}
