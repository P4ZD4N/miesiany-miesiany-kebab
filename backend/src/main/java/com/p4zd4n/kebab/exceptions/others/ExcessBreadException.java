package com.p4zd4n.kebab.exceptions.others;

public class ExcessBreadException extends RuntimeException {

  public ExcessBreadException() {
    super("This meal already has bread!");
  }
}
