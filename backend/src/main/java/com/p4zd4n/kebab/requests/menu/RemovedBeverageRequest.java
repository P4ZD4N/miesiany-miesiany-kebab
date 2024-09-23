package com.p4zd4n.kebab.requests.menu;

import lombok.Builder;

@Builder
public record RemovedBeverageRequest(
        String name
) {}
