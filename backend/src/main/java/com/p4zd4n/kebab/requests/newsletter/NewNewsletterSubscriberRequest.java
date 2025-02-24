package com.p4zd4n.kebab.requests.newsletter;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record NewNewsletterSubscriberRequest(

        @JsonProperty("first_name")
        @NotBlank(message = "{firstName.notBlank}")
        @Size(min = 1, max = 20, message = "{firstName.between1And20}")
        String firstName,

        @JsonProperty("email")
        @NotBlank(message = "{email.notBlank}")
        @Size(min = 1, max = 35, message = "{email.between1And35}")
        @Email(
            regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "{email.invalidFormat}"
        )
        String email
) {}
