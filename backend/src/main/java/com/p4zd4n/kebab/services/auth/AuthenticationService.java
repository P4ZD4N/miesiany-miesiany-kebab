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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {

    private final EmployeeRepository employeeRepository;

    public AuthenticationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

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
            log.info("Successfully authenticated manager with email: {}", request.email());

            return AuthenticationResponse
                    .builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Authenticated manager with email '" + request.email() + "'!")
                    .role(Role.MANAGER)
                    .build();
        }

        log.info("Successfully authenticated employee with email: {}", request.email());

        return AuthenticationResponse
                .builder()
                .statusCode(HttpStatus.OK.value())
                .message("Authenticated employee with email '" + request.email() + "'!")
                .role(Role.EMPLOYEE)
                .build();
    }
}
