package com.p4zd4n.kebab.exceptions.invalid;

import java.math.BigDecimal;

public class InvalidCapacityException extends RuntimeException {

    public InvalidCapacityException(BigDecimal capacity) {
        super("Invalid capacity: " + capacity);
    }
}
