package com.p4zd4n.kebab.exceptions.others;

public class NullEndDateException extends RuntimeException {
  public NullEndDateException() {
    super("End date cannot be null");
  }
}
