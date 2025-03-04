package com.p4zd4n.kebab.exceptions.failed;

import lombok.Getter;

@Getter
public class OtpRegenerationFailedException extends RuntimeException {

    private final int otoRegenerationTimeSeconds;

    public OtpRegenerationFailedException(int otoRegenerationTimeSeconds) {
        super("Unable to regenerate OTP");
        this.otoRegenerationTimeSeconds = otoRegenerationTimeSeconds;
    }
}
