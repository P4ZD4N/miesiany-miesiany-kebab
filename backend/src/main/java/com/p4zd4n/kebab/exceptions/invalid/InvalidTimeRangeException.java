package com.p4zd4n.kebab.exceptions.invalid;

import java.time.LocalTime;
import lombok.Getter;

@Getter
public class InvalidTimeRangeException extends RuntimeException {

  private final LocalTime startTime;
  private final LocalTime endTime;

  public InvalidTimeRangeException(LocalTime startTime, LocalTime endTime) {
    super("Start time '" + startTime + "' must be earlier than end time '" + endTime + "'");
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
