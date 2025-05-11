package com.p4zd4n.kebab.exceptions.overlap;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class WorkScheduleTimeOverlapException extends RuntimeException {
    private final String employeeEmail;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public WorkScheduleTimeOverlapException(
            String employeeEmail, LocalDate date, LocalTime startTime, LocalTime endTime
    ) {
        super(
            "Work schedule entry for employee with email '" +
            employeeEmail +
            "' on " +
            date +
            " from " +
            startTime +
            " to " +
            endTime +
            " overlaps with existing entry"
        );
        this.employeeEmail = employeeEmail;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
