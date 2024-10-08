package com.p4zd4n.kebab.requests.menu.addons;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RemovedAddonRequest(

        @Size(min = 1, message = "{name.greaterThanZero}")
        String name
) {}
