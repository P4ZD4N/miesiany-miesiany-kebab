package com.p4zd4n.kebab.exceptions;

public class InvalidAcceptLanguageHeaderValue extends RuntimeException {

    public InvalidAcceptLanguageHeaderValue(String invalidLanguage) {
        super("Invalid Accept-Language header value: " + invalidLanguage);
    }
}
