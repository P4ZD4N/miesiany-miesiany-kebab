package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class OrderNotFoundException extends RuntimeException {

    private final Long id;

    public OrderNotFoundException(Long id) {
        super("Order with id '" + id + "' not found!");
        this.id = id;
    }
}
