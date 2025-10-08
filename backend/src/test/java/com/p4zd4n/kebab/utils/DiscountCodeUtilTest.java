package com.p4zd4n.kebab.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class DiscountCodeUtilTest {

  @Test
  public void generateDiscountCode_ShouldReturnSixteenDigitCode() {

    String code = DiscountCodeUtil.generateDiscountCode();

    assertEquals(16, code.length());
  }

  @RepeatedTest(10)
  public void generateDiscountCode_ShouldGenerateDifferentValues() {

    String code1 = DiscountCodeUtil.generateDiscountCode();
    String code2 = DiscountCodeUtil.generateDiscountCode();

    assertNotEquals(code1, code2);
  }
}
