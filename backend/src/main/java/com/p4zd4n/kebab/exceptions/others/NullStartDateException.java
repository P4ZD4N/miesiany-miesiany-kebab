package com.p4zd4n.kebab.exceptions.others;

public class NullStartDateException extends RuntimeException {
    public NullStartDateException() {
        super("Start date cannot be null");
    }
}
