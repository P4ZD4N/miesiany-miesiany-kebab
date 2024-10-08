package com.p4zd4n.kebab.exceptions.invalid;

import lombok.Getter;

@Getter
public class InvalidAcceptLanguageHeaderValue extends RuntimeException {

    private final String invalidValue;

    public InvalidAcceptLanguageHeaderValue(String invalidLanguage) {
        super("Invalid Accept-Language header value: " + invalidLanguage);
        invalidValue = invalidLanguage;
    }
}
