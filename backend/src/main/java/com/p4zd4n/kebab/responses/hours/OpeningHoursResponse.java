package com.p4zd4n.kebab.responses.hours;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.DayOfWeek;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record OpeningHoursResponse(
        @JsonProperty("day_of_week") DayOfWeek dayOfWeek,
        @JsonProperty("opening_time") LocalTime openingTime,
        @JsonProperty("closing_time") LocalTime closingTime
) {}
