package com.p4zd4n.kebab.responses.workschedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.entities.Employee;

import jakarta.persistence.Column;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record WorkScheduleEntryResponse(

        Long id,
        @JsonProperty("employee_first_name") String employeeFirstName,
        @JsonProperty("employee_last_name") String employeeLastName,
        @JsonProperty("employee_email") String employeeEmail,
        LocalDate date,
        @JsonProperty("start_time") LocalTime startTime,
        @JsonProperty("end_time") LocalTime endTime
) {}
