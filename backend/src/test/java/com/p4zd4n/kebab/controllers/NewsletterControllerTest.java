package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.repositories.NewsletterRepository;
import com.p4zd4n.kebab.responses.newsletter.NewsletterSubscriberResponse;
import com.p4zd4n.kebab.services.newsletter.NewsletterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewsletterController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NewsletterControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsletterRepository newsletterRepository;

    @MockBean
    private NewsletterService newsletterService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getSubscribers_ShouldReturnSubscribers_WhenCalled() throws Exception {

        List<NewsletterSubscriberResponse> subscribers = Arrays.asList(
                NewsletterSubscriberResponse.builder()
                        .firstName("John")
                        .email("johndoe@example.com")
                        .isActive(true)
                        .build(),
                NewsletterSubscriberResponse.builder()
                        .firstName("Sarah")
                        .email("sarahdoe@example.com")
                        .isActive(false)
                        .build()
        );

        when(newsletterService.getSubscribers()).thenReturn(subscribers);

        mockMvc.perform(get("/api/v1/newsletter/subscribers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is("johndoe@example.com")))
                .andExpect(jsonPath("$[0].is_active", is(true)))
                .andExpect(jsonPath("$[1].first_name", is("Sarah")))
                .andExpect(jsonPath("$[1].is_active", is(false)));

        verify(newsletterService, times(1)).getSubscribers();
    }
}
