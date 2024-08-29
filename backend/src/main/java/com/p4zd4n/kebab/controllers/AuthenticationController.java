package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.requests.auth.AuthenticationRequest;
import com.p4zd4n.kebab.responses.auth.AuthenticationResponse;
import com.p4zd4n.kebab.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody AuthenticationRequest request
    ) {
        log.info("Received login request for email '{}'", request.email());
        AuthenticationResponse response = authenticationService.authenticate(request);
        log.info("Employee with email '{}' successfully logged in", request.email());

        return ResponseEntity.ok(response);
    }
}
