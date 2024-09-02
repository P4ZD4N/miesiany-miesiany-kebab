package com.p4zd4n.kebab.exceptions;

import lombok.Getter;

@Getter
public class EmployeeNotActiveException extends RuntimeException {

    private final String email;

    public EmployeeNotActiveException(String email) {
        super("Employee with email '" + email + "' is not active!");
        this.email = email;
    }
}
