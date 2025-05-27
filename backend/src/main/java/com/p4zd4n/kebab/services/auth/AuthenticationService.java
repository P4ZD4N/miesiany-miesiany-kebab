package com.p4zd4n.kebab.services.auth;

import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.entities.Manager;
import com.p4zd4n.kebab.enums.Role;
import com.p4zd4n.kebab.exceptions.notactive.EmployeeNotActiveException;
import com.p4zd4n.kebab.exceptions.notfound.EmployeeNotFoundException;
import com.p4zd4n.kebab.exceptions.invalid.InvalidCredentialsException;
import com.p4zd4n.kebab.repositories.EmployeesRepository;
import com.p4zd4n.kebab.requests.auth.AuthenticationRequest;
import com.p4zd4n.kebab.responses.auth.AuthenticationResponse;
import com.p4zd4n.kebab.responses.auth.LogoutResponse;
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

    private final EmployeesRepository employeesRepository;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(AuthenticationManager authenticationManager, EmployeesRepository employeesRepository) {
        this.authenticationManager = authenticationManager;
        this.employeesRepository = employeesRepository;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpSession session) {

        log.info("Attempting authentication for email '{}'", request.email());

        Employee employee = employeesRepository.findByEmail(request.email())
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
        session.setAttribute("userEmail", request.email());
    }

    public LogoutResponse logout(HttpSession session) {

        String email = (String) session.getAttribute("userEmail");
        LogoutResponse response = LogoutResponse
                .builder()
                .statusCode(HttpStatus.OK.value()).message("Logged out successfully")
                .build();

        session.invalidate();
        log.info("Successfully logged out employee with email '{}'", email);

        return response;
    }
}
