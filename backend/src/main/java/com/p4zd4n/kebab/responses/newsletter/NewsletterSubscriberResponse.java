package com.p4zd4n.kebab.responses.newsletter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NewsletterSubscriberResponse(
        String email,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("messages_language") NewsletterMessagesLanguage newsletterMessagesLanguage,
        @JsonProperty("subscribed_at") LocalDateTime subscribedAt,
        @JsonProperty("is_active") Boolean isActive
) {}
