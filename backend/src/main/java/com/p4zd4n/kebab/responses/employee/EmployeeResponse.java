package com.p4zd4n.kebab.responses.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.EmploymentType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record EmployeeResponse(

        Long id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String email,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("date_of_birth") LocalDate dateOfBirth,
        String job,
        @JsonProperty("employment_type") EmploymentType employmentType,
        @JsonProperty("hourly_wage") BigDecimal hourlyWage,
        @JsonProperty("is_active") boolean isActive,
        @JsonProperty("is_student") boolean isStudent,
        @JsonProperty("hired_at") LocalDate hiredAt
) {}
