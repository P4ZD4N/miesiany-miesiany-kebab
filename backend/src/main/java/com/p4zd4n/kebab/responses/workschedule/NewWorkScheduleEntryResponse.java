package com.p4zd4n.kebab.responses.workschedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record NewWorkScheduleEntryResponse(
        @JsonProperty("status_code") Integer statusCode,
        String message
) {}
