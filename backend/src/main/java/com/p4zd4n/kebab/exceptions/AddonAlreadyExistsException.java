package com.p4zd4n.kebab.exceptions;

public class AddonAlreadyExistsException extends RuntimeException {

    public AddonAlreadyExistsException() {
        super("Addon with this name already exists!");
    }
}
