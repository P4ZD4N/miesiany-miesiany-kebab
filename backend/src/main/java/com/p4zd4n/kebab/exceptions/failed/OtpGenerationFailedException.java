package com.p4zd4n.kebab.exceptions.failed;

public class OtpGenerationFailedException extends RuntimeException {

    public OtpGenerationFailedException(int maxAttempts) {
        super("Unable to generate unique OTP after " + maxAttempts + " attempts");
    }
}
