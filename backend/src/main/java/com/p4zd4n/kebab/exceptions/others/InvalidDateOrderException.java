package com.p4zd4n.kebab.exceptions.others;

public class InvalidDateOrderException extends RuntimeException {
    public InvalidDateOrderException() {
        super("Start date cannot be after end date!");
    }
}
