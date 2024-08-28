package com.p4zd4n.kebab.exceptions;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String email) {
        super("Employee with email '" + email + "' not found!");
    }
}
