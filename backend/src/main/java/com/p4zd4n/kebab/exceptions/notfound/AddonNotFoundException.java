package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class AddonNotFoundException extends RuntimeException {

    private final String addonName;
    public AddonNotFoundException(String name) {
        super("Addon with name '" + name + "' not found!");
        addonName = name;
    }
}
