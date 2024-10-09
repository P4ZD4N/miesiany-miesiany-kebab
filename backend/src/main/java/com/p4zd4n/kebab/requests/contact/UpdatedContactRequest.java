package com.p4zd4n.kebab.requests.contact;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.ContactType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdatedContactRequest(

        @JsonProperty("contact_type")
        @NotNull(message = "{contactType.notNull}")
        ContactType contactType,

        @JsonProperty("new_value")
        @Size(min = 1, message = "{value.greaterThanZero}")
        String newValue
) {}
