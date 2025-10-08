package com.p4zd4n.kebab.utils;

import com.p4zd4n.kebab.exceptions.invalid.InvalidAcceptLanguageHeaderValue;

public class LanguageValidator {

  public static void validateLanguage(String language) {
    if (language == null || language.isBlank()) {
      throw new InvalidAcceptLanguageHeaderValue("empty");
    }

    if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
      throw new InvalidAcceptLanguageHeaderValue(language);
    }
  }
}
