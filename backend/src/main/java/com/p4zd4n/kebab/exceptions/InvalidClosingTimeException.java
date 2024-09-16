package com.p4zd4n.kebab.exceptions;

public class InvalidClosingTimeException extends RuntimeException {

    public InvalidClosingTimeException() {
        super("Closing hour cannot be earlier than opening hour!");
    }
}
