package com.p4zd4n.kebab.exceptions.wrong;

public class WrongPasswordException extends RuntimeException {

  public WrongPasswordException() {
    super("Invalid password was provided for email");
  }
}
