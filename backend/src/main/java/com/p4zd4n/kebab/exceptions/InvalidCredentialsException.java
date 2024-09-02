package com.p4zd4n.kebab.exceptions;

import lombok.Getter;

@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final String email;

    public InvalidCredentialsException(String email) {
        super("Invalid credentials provided for email: " + email);
        this.email = email;
    }
}
