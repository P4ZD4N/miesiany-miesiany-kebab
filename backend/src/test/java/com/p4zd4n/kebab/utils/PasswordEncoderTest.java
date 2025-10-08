package com.p4zd4n.kebab.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PasswordEncoderTest {

  @Test
  public void encodePassword_ShouldEncodePasswordCorrectly() {

    String plainPassword = "password123";
    String encodedPassword = PasswordEncoder.encodePassword(plainPassword);

    assertNotNull(encodedPassword);
    assertNotEquals(plainPassword, encodedPassword);
  }

  @Test
  public void matches_ShouldReturnTrue_WhenPasswordsMatch() {

    String plainPassword = "password123";
    String encodedPassword = PasswordEncoder.encodePassword(plainPassword);

    assertTrue(PasswordEncoder.matches(plainPassword, encodedPassword));
  }

  @Test
  public void matches_ShouldReturnFalse_WhenPasswordsDoNotMatch() {

    String plainPassword = "password123";
    String incorrectPassword = "admin123";
    String encodedPassword = PasswordEncoder.encodePassword(plainPassword);

    assertFalse(PasswordEncoder.matches(incorrectPassword, encodedPassword));
  }
}
