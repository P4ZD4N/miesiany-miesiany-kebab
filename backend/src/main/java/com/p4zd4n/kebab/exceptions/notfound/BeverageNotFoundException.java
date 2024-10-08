package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class BeverageNotFoundException extends RuntimeException {

    private final String beverageName;

    public BeverageNotFoundException(String name) {
        super("Beverage with name '" + name + "' not found!");
        beverageName = name;
    }
}
