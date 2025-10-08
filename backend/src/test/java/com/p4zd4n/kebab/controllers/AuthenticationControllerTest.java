package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.repositories.EmployeesRepository;
import com.p4zd4n.kebab.repositories.OpeningHoursRepository;
import com.p4zd4n.kebab.requests.auth.AuthenticationRequest;
import com.p4zd4n.kebab.responses.auth.AuthenticationResponse;
import com.p4zd4n.kebab.responses.auth.LogoutResponse;
import com.p4zd4n.kebab.services.auth.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

  @MockBean private AuthenticationService authenticationService;

  @MockBean private EmployeesRepository employeeRepository;

  @MockBean private OpeningHoursRepository openingHoursRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvc mockMvc;

  private AuthenticationRequest authenticationRequest;
  private AuthenticationResponse authenticationResponse;
  private HttpSession session;

  @BeforeEach
  void setUp() {
    authenticationRequest =
        AuthenticationRequest.builder().email("user@example.com").password("password123").build();
    authenticationResponse =
        AuthenticationResponse.builder()
            .statusCode(200)
            .message("Authenticated employee with email 'user@example.com'!")
            .build();
    session = Mockito.mock(HttpSession.class);
  }

  @Test
  public void authenticate_ShouldReturnOk_WhenValidRequest() throws Exception {

    String[] headers = {"en", "En", "eN", "EN", "pl", "Pl", "pL", "PL"};

    when(authenticationService.authenticate(authenticationRequest, session))
        .thenReturn(authenticationResponse);

    for (String header : headers) {
      mockMvc
          .perform(
              post("/api/v1/auth/login")
                  .header("Accept-Language", header)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(authenticationRequest)))
          .andExpect(status().isOk());
    }
  }

  @Test
  public void authenticate_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void authenticate_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

    String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

    for (String header : invalidHeaders) {
      mockMvc
          .perform(post("/api/v1/auth/login").header("Accept-Language", header))
          .andExpect(status().isBadRequest());
    }
  }

  @Test
  public void logout_ShouldReturnOk_WhenEmailInSession() throws Exception {

    LogoutResponse logoutResponse =
        LogoutResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Logged out successfully")
            .build();

    when(authenticationService.logout(any(HttpSession.class))).thenReturn(logoutResponse);

    mockMvc
        .perform(post("/api/v1/auth/logout").sessionAttr("userEmail", "test@example.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.message").value("Logged out successfully"));

    verify(authenticationService).logout(any(HttpSession.class));
  }
}
