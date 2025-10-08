package com.p4zd4n.kebab.requests.workschedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record NewWorkScheduleEntryRequest(
    @JsonProperty("employee_email")
        @NotBlank(message = "{email.notBlank}")
        @Size(min = 1, max = 35, message = "{email.between1And35}")
        @Email(
            regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "{email.invalidFormat}")
        String employeeEmail,
    @NotNull(message = "{date.notNull}") LocalDate date,
    @JsonProperty("start_time") @NotNull(message = "{startTime.notNull}") LocalTime startTime,
    @JsonProperty("end_time") @NotNull(message = "{endTime.notNull}") LocalTime endTime) {}
