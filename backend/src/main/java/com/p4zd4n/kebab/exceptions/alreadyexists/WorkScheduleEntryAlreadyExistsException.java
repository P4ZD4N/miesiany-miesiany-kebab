package com.p4zd4n.kebab.exceptions.alreadyexists;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class WorkScheduleEntryAlreadyExistsException extends RuntimeException {

    private final String employeeEmail;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public WorkScheduleEntryAlreadyExistsException(
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
            " already exists");
        this.employeeEmail = employeeEmail;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
