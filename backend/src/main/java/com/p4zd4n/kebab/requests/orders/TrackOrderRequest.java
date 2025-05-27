package com.p4zd4n.kebab.requests.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record TrackOrderRequest(

        @NotNull(message = "{id.notNull}")
        Long id,

        @JsonProperty("customer_phone")
        @NotBlank(message = "{phone.notBlank}")
        @Pattern(regexp = "^[0-9]{9}$", message = "{phone.invalidFormat}")
        String customerPhone
) {}
