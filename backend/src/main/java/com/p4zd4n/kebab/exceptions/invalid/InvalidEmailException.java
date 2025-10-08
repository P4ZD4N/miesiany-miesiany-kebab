package com.p4zd4n.kebab.exceptions.invalid;

import lombok.Getter;

@Getter
public class InvalidEmailException extends RuntimeException {

  private final String email;

  public InvalidEmailException(String invalidEmail) {
    super("Invalid email: " + invalidEmail);
    email = invalidEmail;
  }
}
