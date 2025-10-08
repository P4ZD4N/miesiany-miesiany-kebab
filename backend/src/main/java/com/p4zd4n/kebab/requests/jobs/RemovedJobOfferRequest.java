package com.p4zd4n.kebab.requests.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RemovedJobOfferRequest(
    @JsonProperty("position_name") @Size(min = 1, message = "{name.greaterThanZero}")
        String positionName) {}
