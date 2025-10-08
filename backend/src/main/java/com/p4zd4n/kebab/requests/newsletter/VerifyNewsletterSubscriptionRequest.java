package com.p4zd4n.kebab.requests.newsletter;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record VerifyNewsletterSubscriptionRequest(
    @NotBlank(message = "{email.notBlank}")
        @Size(min = 1, max = 35, message = "{email.between1And35}")
        @Email(
            regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "{email.invalidFormat}")
        String email,
    @NotNull(message = "{otp.notNull}")
        @Min(value = 100000, message = "{otp.minmax}")
        @Max(value = 999999, message = "{otp.minmax}")
        Integer otp) {}
