package com.p4zd4n.kebab.responses.contact;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.ContactType;
import lombok.Builder;

@Builder
public record ContactResponse(
        @JsonProperty("contact_type") ContactType contactType,
        String value
) {}
