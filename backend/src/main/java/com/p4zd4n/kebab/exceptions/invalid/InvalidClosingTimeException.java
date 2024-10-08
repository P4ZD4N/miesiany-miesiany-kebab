package com.p4zd4n.kebab.exceptions.invalid;

public class InvalidClosingTimeException extends RuntimeException {

    public InvalidClosingTimeException() {
        super("Closing hour cannot be earlier than opening hour!");
    }
}
