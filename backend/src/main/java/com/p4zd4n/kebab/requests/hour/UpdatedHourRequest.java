package com.p4zd4n.kebab.requests.hour;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.DayOfWeek;

import java.time.LocalTime;

public record UpdatedHourRequest(
        @JsonProperty("day_of_week") DayOfWeek dayOfWeek,
        @JsonProperty("opening_time") LocalTime openingTime,
        @JsonProperty("closing_time") LocalTime closingTime
) {}