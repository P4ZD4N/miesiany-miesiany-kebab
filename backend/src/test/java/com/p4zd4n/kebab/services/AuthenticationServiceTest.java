package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.enums.Role;
import com.p4zd4n.kebab.exceptions.EmployeeNotActiveException;
import com.p4zd4n.kebab.exceptions.EmployeeNotFoundException;
import com.p4zd4n.kebab.exceptions.InvalidCredentialsException;
import com.p4zd4n.kebab.repositories.EmployeeRepository;
import com.p4zd4n.kebab.requests.auth.AuthenticationRequest;
import com.p4zd4n.kebab.responses.auth.AuthenticationResponse;
import com.p4zd4n.kebab.services.auth.AuthenticationService;
import com.p4zd4n.kebab.utils.PasswordEncoder;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthenticationServiceTest {

    private AuthenticationManager authenticationManager;
    private AuthenticationService authenticationService;
    private EmployeeRepository employeeRepository;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        employeeRepository = mock(EmployeeRepository.class);
        session = mock(HttpSession.class);
        authenticationService = new AuthenticationService(authenticationManager, employeeRepository);
    }

    @Test
    public void authenticate_ShouldReturnOk_WhenValidRequest() {

        Employee testEmployee = Employee.builder()
                .email("emp@example.com")
                .password(PasswordEncoder.encodePassword("password123"))
                .isActive(true)
                .build();

        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(testEmployee.getEmail())
                .password("password123")
                .build();

        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Authenticated employee with email '" + request.email() + "'!")
                .role(Role.EMPLOYEE)
                .build();

        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(testEmployee));

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        AuthenticationResponse response = authenticationService.authenticate(request, session);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void authenticate_ShouldThrowInvalidCredentialsException_WhenInvalidPassword() {

        Employee testEmployee = Employee.builder()
                .email("emp@example.com")
                .password(PasswordEncoder.encodePassword("password123"))
                .isActive(true)
                .build();

        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(testEmployee.getEmail())
                .password("admin123")
                .build();

        when(employeeRepository.findByEmail(testEmployee.getEmail())).thenReturn(Optional.of(testEmployee));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.authenticate(request, session));
    }


    @Test
    public void authenticate_ShouldThrowEmployeeNotActiveException_WhenInactiveEmployee() {

        Employee testEmployee = Employee.builder()
                .email("emp@example.com")
                .password(PasswordEncoder.encodePassword("password123"))
                .isActive(false)
                .build();

        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(testEmployee.getEmail())
                .password("password123")
                .build();

        when(employeeRepository.findByEmail(testEmployee.getEmail())).thenReturn(Optional.of(testEmployee));

        assertThrows(EmployeeNotActiveException.class, () -> authenticationService.authenticate(request, session));
    }

    @Test
    public void authenticate_ShouldThrowEmployeeNotFoundException_WhenEmployeeNotFound() {

        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("emp@example.com")
                .password("password123")
                .build();

        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> authenticationService.authenticate(request, session));
    }
}
