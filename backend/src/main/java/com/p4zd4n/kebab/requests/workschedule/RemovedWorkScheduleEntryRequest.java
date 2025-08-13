package com.p4zd4n.kebab.requests.workschedule;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RemovedWorkScheduleEntryRequest(
        @NotNull(message = "{id.notNull}")
        Long id
) {}
