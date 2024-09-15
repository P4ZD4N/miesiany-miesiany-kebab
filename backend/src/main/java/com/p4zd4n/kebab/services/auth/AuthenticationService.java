package com.p4zd4n.kebab.services.auth;

import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.entities.Manager;
import com.p4zd4n.kebab.enums.Role;
import com.p4zd4n.kebab.exceptions.EmployeeNotActiveException;
import com.p4zd4n.kebab.exceptions.EmployeeNotFoundException;
import com.p4zd4n.kebab.exceptions.InvalidCredentialsException;
import com.p4zd4n.kebab.repositories.EmployeeRepository;
import com.p4zd4n.kebab.requests.auth.AuthenticationRequest;
import com.p4zd4n.kebab.responses.auth.AuthenticationResponse;
import com.p4zd4n.kebab.utils.PasswordEncoder;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {

    private final EmployeeRepository employeeRepository;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(AuthenticationManager authenticationManager, EmployeeRepository employeeRepository) {
        this.authenticationManager = authenticationManager;
        this.employeeRepository = employeeRepository;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpSession session) {

        log.info("Attempting authentication for email '{}'", request.email());

        Employee employee = employeeRepository.findByEmail(request.email())
                .orElseThrow(() -> new EmployeeNotFoundException(request.email()));

        if (!PasswordEncoder.matches(request.password(), employee.getPassword())) {
            throw new InvalidCredentialsException(request.email());
        }

        if (!employee.isActive()) {
            throw new EmployeeNotActiveException(request.email());
        }

        if (employee instanceof Manager) {
            AuthenticationResponse response = AuthenticationResponse
                    .builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Authenticated manager with email '" + request.email() + "'!")
                    .role(Role.MANAGER)
                    .build();

            setAuthentication(request, session);

            log.info("Successfully authenticated manager with email: {}", request.email());

            return response;
        }

        AuthenticationResponse response = AuthenticationResponse
                .builder()
                .statusCode(HttpStatus.OK.value())
                .message("Authenticated employee with email '" + request.email() + "'!")
                .role(Role.EMPLOYEE)
                .build();

        setAuthentication(request, session);

        log.info("Successfully authenticated employee with email: {}", request.email());

        return response;
    }

    private void setAuthentication(AuthenticationRequest request, HttpSession session) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
}
