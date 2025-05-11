package com.p4zd4n.kebab.responses.workschedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.entities.Employee;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record WorkScheduleEntryResponse(

        Long id,
        Employee employee,
        LocalDate date,
        @JsonProperty("start_time") LocalTime startTime,
        @JsonProperty("end_time") LocalTime endTime
) {}
