package com.p4zd4n.kebab.exceptions;

public class BeverageAlreadyExistsException extends RuntimeException {

    public BeverageAlreadyExistsException() {
        super("Beverage with this name and capacity already exists!");
    }
}
