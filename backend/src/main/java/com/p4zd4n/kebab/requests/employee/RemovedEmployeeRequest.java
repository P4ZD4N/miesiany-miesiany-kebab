package com.p4zd4n.kebab.requests.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RemovedEmployeeRequest(

        @Email(message = "{email.invalidFormat}")
        @Size(min = 1, max = 35, message = "{email.between1And35}")
        String email
) {}
