package com.p4zd4n.kebab.requests.newsletter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegenerateOtpRequest(

        @NotBlank(message = "{email.notBlank}")
        @Size(min = 1, max = 35, message = "{email.between1And35}")
        @Email(
                regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "{email.invalidFormat}"
        )
        String email
) {}
