package com.p4zd4n.kebab.responses.auth;

import com.p4zd4n.kebab.enums.Role;
import lombok.Builder;

@Builder
public record SessionCheckResponse(
        boolean authenticated,
        String email,
        Role role
) {}