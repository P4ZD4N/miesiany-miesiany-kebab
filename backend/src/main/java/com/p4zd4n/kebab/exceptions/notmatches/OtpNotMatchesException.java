package com.p4zd4n.kebab.exceptions.notmatches;

public class OtpNotMatchesException extends RuntimeException {
    public OtpNotMatchesException() {
        super("OTP not matches!");
    }
}
