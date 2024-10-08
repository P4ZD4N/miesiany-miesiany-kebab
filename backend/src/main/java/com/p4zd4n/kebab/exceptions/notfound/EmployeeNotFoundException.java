package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class EmployeeNotFoundException extends RuntimeException {

    private final String email;

    public EmployeeNotFoundException(String email) {
        super("Employee with email '" + email + "' not found!");
        this.email = email;
    }

}
