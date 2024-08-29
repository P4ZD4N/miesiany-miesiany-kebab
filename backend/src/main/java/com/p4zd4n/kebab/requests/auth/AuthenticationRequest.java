package com.p4zd4n.kebab.requests.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(

        @NotBlank(message = "{email.notBlank}")
        @Email(message = "{email.invalidFormat}")
        String email,

        @NotBlank(message = "{password.notBlank}")
        String password
) {}
