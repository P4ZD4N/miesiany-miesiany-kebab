package com.p4zd4n.kebab.exceptions.alreadyexists;

import lombok.Getter;

@Getter
public class EmployeeAlreadyExistsException extends RuntimeException {

    private final String email;

    public EmployeeAlreadyExistsException(String email) {
        super("Employee with email '" + email + "' already exists!");
        this.email = email;
    }
}
