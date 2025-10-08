package com.p4zd4n.kebab.exceptions.expired;

public class OtpExpiredException extends RuntimeException {
  public OtpExpiredException() {
    super("OTP expired!");
  }
}
