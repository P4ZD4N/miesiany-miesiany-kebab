package com.p4zd4n.kebab.exceptions.expired;

import lombok.Getter;

@Getter
public class DiscountCodeExpiredException extends RuntimeException {

    private final String code;

    public DiscountCodeExpiredException(String code) {
        super("Discount code '" + code + "' expired!");
        this.code = code;
    }
}
