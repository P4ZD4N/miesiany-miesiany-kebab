package com.p4zd4n.kebab.utils;

import com.p4zd4n.kebab.exceptions.invalid.InvalidAcceptLanguageHeaderValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageValidatorTest {

  @Test
  public void validateLanguage_ShouldThrowInvalidAcceptLanguageHeaderValue_WhenLanguageIsNull() {
    InvalidAcceptLanguageHeaderValue exception =
        assertThrows(
            InvalidAcceptLanguageHeaderValue.class, () -> LanguageValidator.validateLanguage(null));

    assertEquals("Invalid Accept-Language header value: empty", exception.getMessage());
  }

  @Test
  public void validateLanguage_ShouldThrowInvalidAcceptLanguageHeaderValue_WhenLanguageIsBlank() {
    InvalidAcceptLanguageHeaderValue exception =
        assertThrows(
            InvalidAcceptLanguageHeaderValue.class, () -> LanguageValidator.validateLanguage(" "));

    assertEquals("Invalid Accept-Language header value: empty", exception.getMessage());
  }

  @Test
  public void
      validateLanguage_ShouldThrowInvalidAcceptLanguageHeaderValue_WhenLanguageIsUnsupported() {
    String unsupported = "de";
    InvalidAcceptLanguageHeaderValue exception =
        assertThrows(
            InvalidAcceptLanguageHeaderValue.class,
            () -> LanguageValidator.validateLanguage(unsupported));

    assertEquals("Invalid Accept-Language header value: " + unsupported, exception.getMessage());
  }

  @Test
  public void validateLanguage_ShouldNotThrowException_WhenLanguageIsEnglishLowerCase() {
    assertDoesNotThrow(() -> LanguageValidator.validateLanguage("en"));
  }

  @Test
  public void validateLanguage_ShouldNotThrowException_WhenLanguageIsPolishUpperCase() {
    assertDoesNotThrow(() -> LanguageValidator.validateLanguage("PL"));
  }
}
