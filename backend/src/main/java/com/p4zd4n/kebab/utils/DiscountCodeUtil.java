package com.p4zd4n.kebab.utils;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class DiscountCodeUtil {

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int DEFAULT_CODE_LENGTH = 16;
  private static final SecureRandom random = new SecureRandom();

  public static String generateDiscountCode() {
    StringBuilder code = new StringBuilder(DEFAULT_CODE_LENGTH);

    for (int i = 0; i < DEFAULT_CODE_LENGTH; i++) {
      int index = random.nextInt(CHARACTERS.length());
      code.append(CHARACTERS.charAt(index));
    }

    return code.toString();
  }
}
