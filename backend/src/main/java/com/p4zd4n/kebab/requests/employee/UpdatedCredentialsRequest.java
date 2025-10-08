package com.p4zd4n.kebab.requests.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdatedCredentialsRequest(
    @JsonProperty("updated_email")
        @Email(message = "{email.invalidFormat}")
        @Size(max = 35, message = "{email.between1And35}")
        String updatedEmail,
    String password,
    @JsonProperty("updated_password") @Size(min = 5, message = "{password.greaterThan5}")
        String updatedPassword)
    implements EmailUpdateRequest {}
