package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.requests.auth.AuthenticationRequest;
import com.p4zd4n.kebab.responses.auth.AuthenticationResponse;
import com.p4zd4n.kebab.responses.auth.LogoutResponse;
import com.p4zd4n.kebab.services.auth.AuthenticationService;
import com.p4zd4n.kebab.utils.LanguageValidator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody AuthenticationRequest request,
            HttpSession session
    ) {
        LanguageValidator.validateLanguage(language);

        log.info("Received login request for email '{}'", request.email());
        AuthenticationResponse response = authenticationService.authenticate(request, session);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");

        log.info("Received logout request for email '{}'", email);

        LogoutResponse response = authenticationService.logout(session);

        return ResponseEntity.ok(response);
    }
}
