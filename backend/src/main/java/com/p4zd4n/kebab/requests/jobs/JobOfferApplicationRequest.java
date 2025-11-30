package com.p4zd4n.kebab.requests.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record JobOfferApplicationRequest(
    @JsonProperty("position_name") @Size(min = 1, message = "{name.greaterThanZero}")
        String positionName,
    @JsonProperty("applicant_first_name")
        @NotBlank(message = "{firstName.notBlank}")
        @Size(min = 1, max = 20, message = "{firstName.between1And20}")
        String applicantFirstName,
    @JsonProperty("applicant_last_name")
        @NotBlank(message = "{lastName.notBlank}")
        @Size(min = 1, max = 20, message = "{lastName.between1And20}")
        String applicantLastName,
    @JsonProperty("applicant_email")
        @NotBlank(message = "{email.notBlank}")
        @Size(min = 1, max = 35, message = "{email.between1And35}")
        @Email(message = "{email.invalidFormat}")
        String applicantEmail,
    @JsonProperty("applicant_telephone")
        @NotBlank(message = "{phone.notBlank}")
        @Pattern(regexp = "^[0-9]{9}$", message = "{phone.invalidFormat}")
        String applicantTelephone,
    @JsonProperty("additional_message")
        @Size(min = 1, max = 250, message = "{additionalMessage.between1And250}")
        String additionalMessage,
    @JsonProperty("is_student") @NotNull(message = "{isStudent.notNull}") Boolean isStudent) {}
