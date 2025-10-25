package com.p4zd4n.kebab.services.auth;

import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.entities.Manager;
import com.p4zd4n.kebab.enums.Role;
import com.p4zd4n.kebab.exceptions.invalid.InvalidCredentialsException;
import com.p4zd4n.kebab.exceptions.notactive.EmployeeNotActiveException;
import com.p4zd4n.kebab.exceptions.notfound.EmployeeNotFoundException;
import com.p4zd4n.kebab.repositories.EmployeesRepository;
import com.p4zd4n.kebab.requests.auth.AuthenticationRequest;
import com.p4zd4n.kebab.responses.auth.AuthenticationResponse;
import com.p4zd4n.kebab.responses.auth.LogoutResponse;
import com.p4zd4n.kebab.responses.auth.SessionCheckResponse;
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

  public AuthenticationService(
      AuthenticationManager authenticationManager, EmployeesRepository employeesRepository) {
    this.authenticationManager = authenticationManager;
    this.employeesRepository = employeesRepository;
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request, HttpSession session) {

    log.info("Attempting authentication for email '{}'", request.email());

    Employee employee =
        employeesRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new EmployeeNotFoundException(request.email()));

    if (!PasswordEncoder.matches(request.password(), employee.getPassword())) {
      throw new InvalidCredentialsException(request.email());
    }

    if (!employee.isActive()) {
      throw new EmployeeNotActiveException(request.email());
    }

    setAuthentication(request, session);

    Role role = (employee instanceof Manager) ? Role.MANAGER : Role.EMPLOYEE;

    session.setAttribute("userEmail", request.email());
    session.setAttribute("userRole", role);
    session.setMaxInactiveInterval(3600);

    log.info("Successfully authenticated {} with email: {}", role, request.email());

    return AuthenticationResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Authentication successful")
            .role(role)
            .build();
  }

  private void setAuthentication(AuthenticationRequest request, HttpSession session) {

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
  }

  public LogoutResponse logout(HttpSession session) {

    String email = (String) session.getAttribute("userEmail");
    LogoutResponse response =
        LogoutResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Logged out successfully")
            .build();

    session.invalidate();
    log.info("Successfully logged out employee with email '{}'", email);

    return response;
  }

  public SessionCheckResponse getCheckSessionResponse(HttpSession session) {
    String email = (String) session.getAttribute("userEmail");
    Role role = (Role) session.getAttribute("userRole");

    if (email == null || role == null) {
      log.info("Check-session request failed because of no authenticated user right now");

      return SessionCheckResponse.builder()
          .authenticated(false)
          .build();
    }

    log.info("Currently authenticated user with email: '{}' and role: '{}'", email, role);

    return SessionCheckResponse.builder()
            .authenticated(true)
            .email(email)
            .role(role)
            .build();
  }
}
