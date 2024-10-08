package com.p4zd4n.kebab.exceptions.invalid;

import java.math.BigDecimal;

public class InvalidPriceException extends RuntimeException {

    public InvalidPriceException(BigDecimal price) {
        super("Invalid price: " + price);
    }
}
