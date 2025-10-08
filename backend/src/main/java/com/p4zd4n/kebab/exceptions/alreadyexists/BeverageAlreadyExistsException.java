package com.p4zd4n.kebab.exceptions.alreadyexists;

public class BeverageAlreadyExistsException extends RuntimeException {

  public BeverageAlreadyExistsException() {
    super("Beverage with this name and capacity already exists!");
  }
}
