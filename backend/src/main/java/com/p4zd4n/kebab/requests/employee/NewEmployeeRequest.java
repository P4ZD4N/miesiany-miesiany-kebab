package com.p4zd4n.kebab.requests.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.EmploymentType;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record NewEmployeeRequest(

        @JsonProperty("first_name")
        @Size(min = 1, max = 20, message = "{firstName.between1And20}")
        String firstName,

        @JsonProperty("last_name")
        @Size(min = 1, max = 20, message = "{lastName.between1And20}")
        String lastName,

        @Email(message = "{email.invalidFormat}")
        @Size(min = 1, max = 35, message = "{email.between1And35}")
        String email,

        @Size(min = 5, message = "{password.greaterThan5}")
        String password,

        @Pattern(regexp = "^[0-9]{9}$", message = "{phone.invalidFormat}")
        String phone,

        @Size(min = 1, max = 16, message = "{job.between1And16}")
        String job,

        @JsonProperty("hourly_wage")
        @DecimalMin(value = "0.0", inclusive = false, message = "{hourlyWage.greaterThanZero}")
        @NotNull(message = "{hourlyWage.notNull}")
        BigDecimal hourlyWage,

        @JsonProperty("date_of_birth")
        @NotNull(message = "{dateOfBirth.notNull}")
        LocalDate dateOfBirth,

        @JsonProperty("employment_type")
        @NotNull(message = "{employmentType.notNull}")
        EmploymentType employmentType
) {}
