package com.p4zd4n.kebab.exceptions;

public class EmployeeNotActiveException extends RuntimeException {

    public EmployeeNotActiveException(String email) {
        super("Employee with email '" + email + "' is not active!");
    }
}
