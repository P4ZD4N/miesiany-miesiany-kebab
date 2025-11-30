package com.p4zd4n.kebab.requests.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.EmploymentType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UpdatedEmployeeRequest(
    @JsonProperty("employee_email")
        @NotBlank(message = "{email.notBlank}")
        @Email(message = "{email.invalidFormat}")
        @Size(min = 1, max = 35, message = "{email.between1And35}")
        String employeeEmail,
    @JsonProperty("updated_first_name")
        @NotBlank(message = "{firstName.notBlank}")
        @Size(min = 1, max = 20, message = "{firstName.between1And20}")
        String updatedFirstName,
    @JsonProperty("updated_last_name")
        @NotBlank(message = "{lastName.notBlank}")
        @Size(min = 1, max = 20, message = "{lastName.between1And20}")
        String updatedLastName,
    @JsonProperty("updated_email")
        @NotBlank(message = "{email.notBlank}")
        @Email(message = "{email.invalidFormat}")
        @Size(max = 35, message = "{email.between1And35}")
        String updatedEmail,
    @JsonProperty("updated_password") @Size(min = 5, message = "{password.greaterThan5}")
        String updatedPassword,
    @JsonProperty("updated_phone")
        @Pattern(regexp = "^[0-9]{9}$", message = "{phone.invalidFormat}")
        String updatedPhone,
    @JsonProperty("updated_job") @Size(max = 16, message = "{job.between1And16}") String updatedJob,
    @JsonProperty("updated_hourly_wage")
        @DecimalMin(value = "0.0", inclusive = false, message = "{hourlyWage.greaterThanZero}")
        BigDecimal updatedHourlyWage,
    @JsonProperty("updated_date_of_birth") LocalDate updatedDateOfBirth,
    @JsonProperty("updated_employment_type") EmploymentType updatedEmploymentType,
    @JsonProperty("updated_active") Boolean updatedActive,
    @JsonProperty("updated_student") Boolean updatedStudent,
    @JsonProperty("updated_hired_date") LocalDate updatedHiredDate)
    implements EmailUpdateRequest {}
