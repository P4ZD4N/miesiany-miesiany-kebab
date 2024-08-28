package com.p4zd4n.kebab.requests.auth;

public record AuthenticationRequest(
        String email,
        String password
) {}
