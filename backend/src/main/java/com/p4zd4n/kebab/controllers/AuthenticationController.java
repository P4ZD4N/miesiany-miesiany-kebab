package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.requests.auth.AuthenticationRequest;
import com.p4zd4n.kebab.responses.auth.AuthenticationResponse;
import com.p4zd4n.kebab.services.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody AuthenticationRequest request
    ) {
        log.info("Received login request for email '{}'", request.email());
        AuthenticationResponse response = authenticationService.authenticate(request);
        log.info("Employee with email '{}' successfully logged in", request.email());

        return ResponseEntity.ok(response);
    }
}
