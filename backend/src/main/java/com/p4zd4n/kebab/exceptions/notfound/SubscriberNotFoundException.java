package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class SubscriberNotFoundException extends RuntimeException {

  private final String email;

  public SubscriberNotFoundException(String email) {
    super("Subscriber with email '" + email + "' not found!");
    this.email = email;
  }
}
