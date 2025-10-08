package com.p4zd4n.kebab.exceptions.invalid;

import lombok.Getter;

@Getter
public class InvalidPhoneException extends RuntimeException {

  private final String phone;

  public InvalidPhoneException(String invalidPhone) {
    super("Invalid phone: " + invalidPhone + ". Valid phone format: 123456789");
    phone = invalidPhone;
  }
}
