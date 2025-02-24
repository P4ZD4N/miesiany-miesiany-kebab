package com.p4zd4n.kebab.responses.newsletter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NewsletterSubscriberResponse(
        String email,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("subscribed_at") LocalDateTime subscribedAt,
        @JsonProperty("is_active") Boolean isActive
) {}
