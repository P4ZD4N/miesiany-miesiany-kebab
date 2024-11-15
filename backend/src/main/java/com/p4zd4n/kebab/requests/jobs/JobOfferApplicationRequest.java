package com.p4zd4n.kebab.requests.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record JobOfferApplicationRequest(

        @JsonProperty("position_name")
        @Size(min = 1, message = "{name.greaterThanZero}")
        String positionName,

        @JsonProperty("applicant_first_name")
        @Size(min = 1, message = "{firstName.greaterThanZero}")
        String applicantFirstName,

        @JsonProperty("applicant_last_name")
        @Size(min = 1, message = "{last.greaterThanZero}")
        String applicantLastName,

        @JsonProperty("applicant_email")
        @NotBlank(message = "{email.notBlank}")
        @Email(message = "{email.invalidFormat}")
        String applicantEmail,

        @JsonProperty("applicant_telephone")
        @NotBlank(message = "{phone.notBlank}")
        @Pattern(regexp = "^[0-9]{9}$", message = "{phone.invalidFormat}")
        String applicantTelephone,

        @JsonProperty("additional_message")
        String additionalMessage,

        @JsonProperty("is_student")
        @NotNull(message = "{isStudent.notNull}")
        Boolean isStudent
) {}
