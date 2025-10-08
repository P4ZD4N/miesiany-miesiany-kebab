package com.p4zd4n.kebab.exceptions.notfound;

import com.p4zd4n.kebab.enums.DayOfWeek;
import lombok.Getter;

@Getter
public class OpeningHourNotFoundException extends RuntimeException {

  private final DayOfWeek dayOfWeek;

  public OpeningHourNotFoundException(DayOfWeek dayOfWeek) {
    super("Opening hour for day: " + dayOfWeek + " not found!");
    this.dayOfWeek = dayOfWeek;
  }
}
