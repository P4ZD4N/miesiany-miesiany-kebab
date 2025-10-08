package com.p4zd4n.kebab.exceptions.others;

import lombok.Builder;

@Builder
public class ManagerDemotionNotAllowedException extends RuntimeException {

  public ManagerDemotionNotAllowedException() {
    super("Changing position of manager to another role is not allowed!");
  }
}
