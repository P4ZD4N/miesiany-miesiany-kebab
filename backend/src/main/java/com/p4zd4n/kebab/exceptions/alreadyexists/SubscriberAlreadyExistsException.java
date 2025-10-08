package com.p4zd4n.kebab.exceptions.alreadyexists;

import lombok.Getter;

@Getter
public class SubscriberAlreadyExistsException extends RuntimeException {

  private final String email;

  public SubscriberAlreadyExistsException(String email) {
    super("Subscriber with email '" + email + "' already exists!");
    this.email = email;
  }
}
