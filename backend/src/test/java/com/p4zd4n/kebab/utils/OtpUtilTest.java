package com.p4zd4n.kebab.utils;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OtpUtilTest {

    private final OtpUtil otpUtil = new OtpUtil();

    @Test
    public void generateOtp_ShouldReturnSixDigitNumber() {

        Integer otp = otpUtil.generateOtp();

        assertTrue(otp >= 100_000 && otp <= 999_999);
    }

    @RepeatedTest(5)
    public void generateOtp_ShouldGenerateDifferentValues() {

        Integer otp1 = otpUtil.generateOtp();
        Integer otp2 = otpUtil.generateOtp();

        assertTrue(!otp1.equals(otp2));
    }
}
