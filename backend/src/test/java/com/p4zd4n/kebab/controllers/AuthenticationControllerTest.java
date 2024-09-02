package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.repositories.EmployeeRepository;
import com.p4zd4n.kebab.requests.auth.AuthenticationRequest;
import com.p4zd4n.kebab.responses.auth.AuthenticationResponse;
import com.p4zd4n.kebab.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private AuthenticationRequest request;
    private AuthenticationResponse response;

    @BeforeEach
    void setUp() {
        request = AuthenticationRequest
                .builder()
                .email("user@example.com")
                .password("password123")
                .build();
        response = AuthenticationResponse
                .builder()
                .statusCode(200)
                .message("Authenticated employee with email 'user@example.com'!")
                .build();
    }

    @Test
    public void authenticate_ShouldReturnOk_WhenValidRequest() throws Exception {

        String[] headers = {"en", "En", "eN", "EN", "pl", "Pl", "pL", "PL"};

        when(authenticationService.authenticate(request)).thenReturn(response);

        for (String header : headers) {
            mockMvc.perform(post("/api/v1/auth/login")
                    .header("Accept-Language", header)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(response)));
        }
    }

    @Test
    public void authenticate_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void authenticate_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(post("/api/v1/auth/login")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }
}