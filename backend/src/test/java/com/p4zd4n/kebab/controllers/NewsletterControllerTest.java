package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
import com.p4zd4n.kebab.exceptions.GlobalExceptionHandler;
import com.p4zd4n.kebab.exceptions.alreadyexists.SubscriberAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.expired.OtpExpiredException;
import com.p4zd4n.kebab.exceptions.failed.OtpRegenerationFailedException;
import com.p4zd4n.kebab.exceptions.notfound.SubscriberNotFoundException;
import com.p4zd4n.kebab.exceptions.notmatches.OtpNotMatchesException;
import com.p4zd4n.kebab.repositories.NewsletterRepository;
import com.p4zd4n.kebab.requests.newsletter.NewNewsletterSubscriberRequest;
import com.p4zd4n.kebab.requests.newsletter.RegenerateOtpRequest;
import com.p4zd4n.kebab.requests.newsletter.UnsubscribeRequest;
import com.p4zd4n.kebab.requests.newsletter.VerifyNewsletterSubscriptionRequest;
import com.p4zd4n.kebab.responses.newsletter.*;
import com.p4zd4n.kebab.services.newsletter.NewsletterService;
import com.p4zd4n.kebab.utils.OtpUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    public void subscribe_ShouldReturnOk_WhenValidRequest() throws Exception {

        NewNewsletterSubscriberRequest request = NewNewsletterSubscriberRequest.builder()
                .firstName("Wiktor")
                .email("example@example.com")
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .build();

        NewNewsletterSubscriberResponse response = NewNewsletterSubscriberResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new newsletter subscriber with email '" + request.email() + "'")
                .build();

        when(newsletterService.addNewsletterSubscriber(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/newsletter/subscribe")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully added new newsletter subscriber with email 'example@example.com'")));
    }

    @Test
    public void subscribe_ShouldReturnConflict_WhenSubscriberWithSameEmailAlreadyExists() throws Exception {

        NewNewsletterSubscriberRequest request = NewNewsletterSubscriberRequest.builder()
                .firstName("Wiktor")
                .email("example@example.com")
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .build();

        when(newsletterService.addNewsletterSubscriber(request)).thenThrow(new SubscriberAlreadyExistsException(request.email()));

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("subscriber.alreadyExists", null, Locale.forLanguageTag("en")))
                .thenReturn("Subscriber with the same email already exists!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new NewsletterController(newsletterService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(post("/api/v1/newsletter/subscribe")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("Subscriber with the same email already exists!")));

        verify(newsletterService, times(1)).addNewsletterSubscriber(request);
    }

    @Test
    public void subscribe_ShouldReturnBadRequest_WhenInvalidDescription() throws Exception {

        NewNewsletterSubscriberRequest request = NewNewsletterSubscriberRequest.builder()
                .firstName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .email("example@example.com")
                .build();

        mockMvc.perform(post("/api/v1/newsletter/subscribe")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void subscribe_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        NewNewsletterSubscriberRequest request = NewNewsletterSubscriberRequest.builder()
                .firstName("Wiktor")
                .email("example@example.com")
                .build();

        mockMvc.perform(post("/api/v1/newsletter/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void subscribe_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(post("/api/v1/newsletter/subscribe")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void verifySubscription_ShouldReturnOk_WhenValidRequest() throws Exception {

        VerifyNewsletterSubscriptionRequest request = VerifyNewsletterSubscriptionRequest.builder()
                .otp(123456)
                .email("wiko700@gmail.com")
                .build();

        VerifyNewsletterSubscriptionResponse response = VerifyNewsletterSubscriptionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully verified newsletter subscriber")
                .build();

        when(newsletterService.verifySubscription(request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/newsletter/verify-subscription")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully verified newsletter subscriber")));
    }

    @Test
    public void verifySubscription_ShouldReturnConflict_WhenOtpNotMatches() throws Exception {

        VerifyNewsletterSubscriptionRequest request = VerifyNewsletterSubscriptionRequest.builder()
                .otp(123456)
                .email("wiko700@gmail.com")
                .build();

        when(newsletterService.verifySubscription(request)).thenThrow(new OtpNotMatchesException());

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("otp.notMatching", null, Locale.forLanguageTag("en")))
                .thenReturn("OTP not matching! Please ensure the code was entered correctly!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new NewsletterController(newsletterService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(put("/api/v1/newsletter/verify-subscription")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("OTP not matching! Please ensure the code was entered correctly!")));

        verify(newsletterService, times(1)).verifySubscription(request);
    }

    @Test
    public void verifySubscription_ShouldReturnGone_WhenOtpExpired() throws Exception {

        VerifyNewsletterSubscriptionRequest request = VerifyNewsletterSubscriptionRequest.builder()
                .otp(123456)
                .email("wiko700@gmail.com")
                .build();

        when(newsletterService.verifySubscription(request)).thenThrow(new OtpExpiredException());

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("otp.expired", null, Locale.forLanguageTag("en")))
                .thenReturn("OTP expired! Please regenerate OTP and try again!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new NewsletterController(newsletterService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(put("/api/v1/newsletter/verify-subscription")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status_code", is(410)))
                .andExpect(jsonPath("$.message", is("OTP expired! Please regenerate OTP and try again!")));

        verify(newsletterService, times(1)).verifySubscription(request);
    }

    @Test
    public void verifySubscription_ShouldReturnNotFound_WhenSubscriberNotFound() throws Exception {

        VerifyNewsletterSubscriptionRequest request = VerifyNewsletterSubscriptionRequest.builder()
                .otp(123456)
                .email("wiko700@gmail.com")
                .build();

        when(newsletterService.verifySubscription(request)).thenThrow(new SubscriberNotFoundException(request.email()));

        mockMvc.perform(put("/api/v1/newsletter/verify-subscription")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status_code", is(404)))
                .andExpect(jsonPath("$.message", is("Subscriber with email '" + request.email() + "' not found!")));

        verify(newsletterService, times(1)).verifySubscription(request);
    }

    @Test
    public void verifySubscription_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        VerifyNewsletterSubscriptionRequest request = VerifyNewsletterSubscriptionRequest.builder()
                .otp(123456)
                .email("wiko700@gmail.com")
                .build();

        mockMvc.perform(put("/api/v1/newsletter/verify-subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void verifySubscription_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(put("/api/v1/newsletter/verify-subscription")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void regenerateOtp_ShouldReturnOk_WhenValidRequest() throws Exception {

        RegenerateOtpRequest request = RegenerateOtpRequest.builder()
                .email("example@example.com")
                .build();

        RegenerateOtpResponse response = RegenerateOtpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully regenerated otp for subscriber with email '" + request.email() + "'!")
                .build();

        when(newsletterService.regenerateOtp(request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/newsletter/regenerate-otp")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully regenerated otp for subscriber with email '" + request.email() + "'!")));
    }

    @Test
    public void regenerateOtp_ShouldReturnConflict_WhenOtpRegenerationFails() throws Exception {

        RegenerateOtpRequest request = RegenerateOtpRequest.builder()
                .email("wiko700@gmail.com")
                .build();

        when(newsletterService.regenerateOtp(request)).thenThrow(new OtpRegenerationFailedException(OtpUtil.OTP_REGENERATION_TIME_SECONDS));

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("otp.regenerationFailed", null, Locale.forLanguageTag("en")))
                .thenReturn("OTP regeneration failed! Please try again in a moment!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new NewsletterController(newsletterService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(put("/api/v1/newsletter/regenerate-otp")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("OTP regeneration failed! Please try again in a moment!")));

        verify(newsletterService, times(1)).regenerateOtp(request);
    }

    @Test
    public void regenerateOtp_ShouldReturnNotFound_WhenSubscriberNotFound() throws Exception {

        RegenerateOtpRequest request = RegenerateOtpRequest.builder()
                .email("wiko700@gmail.com")
                .build();

        when(newsletterService.regenerateOtp(request)).thenThrow(new SubscriberNotFoundException(request.email()));

        mockMvc.perform(put("/api/v1/newsletter/regenerate-otp")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status_code", is(404)))
                .andExpect(jsonPath("$.message", is("Subscriber with email '" + request.email() + "' not found!")));

        verify(newsletterService, times(1)).regenerateOtp(request);
    }

    @Test
    public void regenerateOtp_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        RegenerateOtpRequest request = RegenerateOtpRequest.builder()
                .email("wiko700@gmail.com")
                .build();

        mockMvc.perform(put("/api/v1/newsletter/regenerate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void regenerateOtp_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(put("/api/v1/newsletter/regenerate-otp")
                    .header("Accept-Lan~guage", header))
                    .andExpect(status().isBadRequest());
        }
    }

    //

    @Test
    public void unsubscribe_ShouldReturnOk_WhenValidRequest() throws Exception {

        UnsubscribeRequest request = UnsubscribeRequest.builder()
                .email("example@example.com")
                .build();

        UnsubscribeResponse response = UnsubscribeResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully deleted subscriber with email '" + request.email() + "'!")
                .build();

        when(newsletterService.unsubscribe(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/newsletter/unsubscribe")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully deleted subscriber with email '" + request.email() + "'!")));
    }

    @Test
    public void unsubscribe_ShouldReturnNotFound_WhenSubscriberNotFound() throws Exception {

        UnsubscribeRequest request = UnsubscribeRequest.builder()
                .email("wiko700@gmail.com")
                .build();

        when(newsletterService.unsubscribe(request)).thenThrow(new SubscriberNotFoundException(request.email()));

        mockMvc.perform(post("/api/v1/newsletter/unsubscribe")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status_code", is(404)))
                .andExpect(jsonPath("$.message", is("Subscriber with email '" + request.email() + "' not found!")));

        verify(newsletterService, times(1)).unsubscribe(request);
    }

    @Test
    public void unsubscribe_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        RegenerateOtpRequest request = RegenerateOtpRequest.builder()
                .email("wiko700@gmail.com")
                .build();

        mockMvc.perform(post("/api/v1/newsletter/unsubscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void unsubscribe_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(post("/api/v1/newsletter/unsubscribe")
                    .header("Accept-Lan~guage", header))
                    .andExpect(status().isBadRequest());
        }
    }
}
