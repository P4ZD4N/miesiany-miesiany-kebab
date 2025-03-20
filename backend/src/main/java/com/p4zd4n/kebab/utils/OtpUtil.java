package com.p4zd4n.kebab.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpUtil {

    public static final int OTP_EXPIRATION_SECONDS = 10 * 60;
    public static final int OTP_REGENERATION_TIME_SECONDS = 5 * 60;
    private static final int MIN_NUMBER = 100_000;
    private static final int MAX_NUMBER = 999_999;
    private static final SecureRandom random = new SecureRandom();

    public Integer generateOtp() {
        return random.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER;
    }
}
