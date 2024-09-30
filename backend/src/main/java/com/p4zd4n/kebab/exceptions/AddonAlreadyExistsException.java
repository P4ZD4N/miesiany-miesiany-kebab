package com.p4zd4n.kebab.exceptions;

import lombok.Getter;

@Getter
public class AddonAlreadyExistsException extends RuntimeException {

    private final String addonName;

    public AddonAlreadyExistsException(String name) {
        super("Addon with name '" + name + "' already exists!");
        addonName = name;
    }
}
