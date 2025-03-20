package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.NewsletterSubscriber;
import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
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
import com.p4zd4n.kebab.utils.mails.GoodbyeMailUtil;
import com.p4zd4n.kebab.utils.mails.VerificationMailUtil;
import com.p4zd4n.kebab.utils.mails.WelcomeMailUtil;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class NewsletterServiceTest {

    @Mock
    private NewsletterRepository newsletterRepository;

    @Mock
    private OtpUtil otpUtil;

    @Mock
    private VerificationMailUtil verificationMailUtil;

    @Mock
    private WelcomeMailUtil welcomeMailUtil;

    @Mock
    private GoodbyeMailUtil goodbyeMailUtil;

    @InjectMocks
    private NewsletterService newsletterService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getSubscribers_ShouldReturnSubscribers_WhenCalled() {

        List<NewsletterSubscriber> subscribers = List.of(
                NewsletterSubscriber.builder()
                        .subscriberFirstName("John")
                        .email("johndoe@example.com")
                        .isActive(true)
                        .build(),
                NewsletterSubscriber.builder()
                        .subscriberFirstName("Sarah")
                        .email("sarahdoe@example.com")
                        .isActive(true)
                        .build()
        );

        when(newsletterRepository.findAll()).thenReturn(subscribers);

        List<NewsletterSubscriberResponse> result = newsletterService.getSubscribers();

        assertEquals(2, result.size());

        assertEquals("John", result.getFirst().firstName());
        assertEquals(true, result.getFirst().isActive());

        assertEquals("sarahdoe@example.com", result.getLast().email());
        assertEquals(true, result.getLast().isActive());

        verify(newsletterRepository, times(1)).findAll();
    }

    @Test
    public void addNewsletterSubscriber_ShouldAddSubscriber_WhenSubscriberWithTheSameEmailDoesNotExist() throws MessagingException {

        NewNewsletterSubscriberRequest request = NewNewsletterSubscriberRequest.builder()
                .firstName("Wiktor")
                .email("example@example.com")
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        NewsletterSubscriber newSubscriber = NewsletterSubscriber.builder()
                .subscriberFirstName(request.firstName())
                .email(request.email())
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .isActive(false)
                .build();

        when(newsletterRepository.save(any(NewsletterSubscriber.class))).thenReturn(newSubscriber);

        NewNewsletterSubscriberResponse response = newsletterService.addNewsletterSubscriber(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully added new newsletter subscriber with email 'example@example.com'", response.message());

        verify(newsletterRepository, times(1)).findByEmail(request.email());
        verify(newsletterRepository, times(1)).save(any(NewsletterSubscriber.class));
    }

    @Test
    public void addNewsletterSubscriber_ShouldThrowSubscriberAlreadyExistsException_WhenSubscriberWithTheSameEmailExists() {

        NewNewsletterSubscriberRequest request = NewNewsletterSubscriberRequest.builder()
                .firstName("Wiktor")
                .email("example@example.com")
                .build();

        NewsletterSubscriber existingSubscriber = NewsletterSubscriber.builder()
                .subscriberFirstName("Michael")
                .email(request.email())
                .isActive(false)
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.of(existingSubscriber));

        assertThrows(SubscriberAlreadyExistsException.class, () -> {
            newsletterService.addNewsletterSubscriber(request);
        });

        verify(newsletterRepository, times(1)).findByEmail(request.email());
    }

    @Test
    public void verifySubscription_ShouldActivateSubscriber_WhenOtpValidAndSubscriberExists() throws MessagingException {

        NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                .subscriberFirstName("Wiktor")
                .email("example@example.com")
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .isActive(false)
                .otp(123456)
                .otpGeneratedTime(LocalDateTime.now())
                .build();

        VerifyNewsletterSubscriptionRequest request = VerifyNewsletterSubscriptionRequest.builder()
                .email("example@example.com")
                .otp(123456)
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.of(subscriber));
        VerifyNewsletterSubscriptionResponse response = newsletterService.verifySubscription(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully verified newsletter subscriber with 'example@example.com' email!", response.message());
        assertTrue(subscriber.isActive());

        verify(newsletterRepository, times(1)).findByEmail(request.email());
        verify(newsletterRepository, times(1)).save(subscriber);
        verify(welcomeMailUtil, times(1)).sendPl(subscriber);
    }

    @Test
    public void verifySubscription_ShouldThrowOtpNotMatchesException_WhenOtpNotMatches() throws MessagingException {

        NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                .subscriberFirstName("Wiktor")
                .email("example@example.com")
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .isActive(false)
                .otp(123456)
                .otpGeneratedTime(LocalDateTime.now())
                .build();

        VerifyNewsletterSubscriptionRequest request = VerifyNewsletterSubscriptionRequest.builder()
                .email("example@example.com")
                .otp(123457)
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.of(subscriber));

        assertThrows(OtpNotMatchesException.class, () -> newsletterService.verifySubscription(request));

        verify(newsletterRepository, times(1)).findByEmail(request.email());
        verify(newsletterRepository, never()).save(any());
    }

    @Test
    public void verifySubscription_ShouldThrowOtpExpired_WhenOtpExpired() throws MessagingException {

        NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                .subscriberFirstName("Wiktor")
                .email("example@example.com")
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .isActive(false)
                .otp(123456)
                .otpGeneratedTime(LocalDateTime.now().minusSeconds(OtpUtil.OTP_EXPIRATION_SECONDS + 10))
                .build();

        VerifyNewsletterSubscriptionRequest request = VerifyNewsletterSubscriptionRequest.builder()
                .email("example@example.com")
                .otp(123456)
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.of(subscriber));

        assertThrows(OtpExpiredException.class, () -> newsletterService.verifySubscription(request));

        verify(newsletterRepository, times(1)).findByEmail(request.email());
        verify(newsletterRepository, never()).save(any());
    }

    @Test
    public void verifySubscription_ShouldThrowSubscriberNotFoundException_WhenSubscriberDoesNotExist() throws MessagingException {

        VerifyNewsletterSubscriptionRequest request = VerifyNewsletterSubscriptionRequest.builder()
                .email("aaaa@example.com")
                .otp(123456)
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(SubscriberNotFoundException.class, () -> newsletterService.verifySubscription(request));

        verify(newsletterRepository, times(1)).findByEmail(request.email());
        verify(newsletterRepository, never()).save(any());
    }

    @Test
    public void regenerateOtp_ShouldRegenerateOtp_WhenSubscriberExistsAndTimeIsValid() throws MessagingException {

        NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                .subscriberFirstName("Wiktor")
                .email("example@example.com")
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .isActive(false)
                .otp(123456)
                .otpGeneratedTime(LocalDateTime.now().minusSeconds(OtpUtil.OTP_REGENERATION_TIME_SECONDS + 10))
                .build();

        RegenerateOtpRequest request = RegenerateOtpRequest.builder()
                .email("example@example.com")
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.of(subscriber));
        when(otpUtil.generateOtp()).thenReturn(123457);

        RegenerateOtpResponse response = newsletterService.regenerateOtp(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully regenerated otp for subscriber with email '" + request.email() + "'!", response.message());

        verify(newsletterRepository, times(1)).findByEmail(request.email());
        verify(otpUtil, times(1)).generateOtp();
        verify(verificationMailUtil, times(1)).sendPl(subscriber.getEmail(), 123457);
        verify(newsletterRepository, times(1)).save(subscriber);
    }

    @Test
    public void regenerateOtp_ShouldThrowOtpRegenerationFailedException_WhenRegenerationTimeNotPassed() throws MessagingException {

        NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                .subscriberFirstName("Wiktor")
                .email("example@example.com")
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .isActive(false)
                .otp(123456)
                .otpGeneratedTime(LocalDateTime.now().minusSeconds(OtpUtil.OTP_REGENERATION_TIME_SECONDS - 10))
                .build();

        RegenerateOtpRequest request = RegenerateOtpRequest.builder()
                .email("example@example.com")
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.of(subscriber));

        assertThrows(OtpRegenerationFailedException.class, () -> newsletterService.regenerateOtp(request));

        verify(newsletterRepository, times(1)).findByEmail(request.email());
        verify(otpUtil, never()).generateOtp();
        verify(verificationMailUtil, never()).sendEng(subscriber.getEmail(), 132543);
        verify(newsletterRepository, never()).save(any());
    }

    @Test
    public void regenerateOtp_ShouldThrowSubscriberNotFoundException_WhenSubscriberDoesNotExist() throws MessagingException {

        RegenerateOtpRequest request = RegenerateOtpRequest.builder()
                .email("example@example.com")
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(SubscriberNotFoundException.class, () -> newsletterService.regenerateOtp(request));

        verify(newsletterRepository, times(1)).findByEmail(request.email());
        verify(otpUtil, never()).generateOtp();
        verify(verificationMailUtil, never()).sendEng(request.email(), 132543);
        verify(newsletterRepository, never()).save(any());
    }

    //

    @Test
    public void unsubscribe_ShouldDeleteSubscriber_WhenSubscriberExists() throws MessagingException {

        NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                .subscriberFirstName("Wiktor")
                .email("example@example.com")
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .isActive(false)
                .otp(123456)
                .otpGeneratedTime(LocalDateTime.now().minusSeconds(OtpUtil.OTP_REGENERATION_TIME_SECONDS + 10))
                .build();

        UnsubscribeRequest request = UnsubscribeRequest.builder()
                .email("example@example.com")
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.of(subscriber));

        UnsubscribeResponse response = newsletterService.unsubscribe(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully deleted subscriber with email '" + request.email() + "'!", response.message());

        verify(newsletterRepository, times(1)).findByEmail(request.email());
        verify(goodbyeMailUtil, times(1)).sendPl(subscriber);
        verify(newsletterRepository, times(1)).delete(subscriber);
    }

    @Test
    public void unsubscribe_ShouldThrowSubscriberNotFoundException_WhenSubscriberDoesNotExist() throws MessagingException {

        UnsubscribeRequest request = UnsubscribeRequest.builder()
                .email("example@example.com")
                .build();

        when(newsletterRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(SubscriberNotFoundException.class, () -> newsletterService.unsubscribe(request));

        verify(newsletterRepository, times(1)).findByEmail(request.email());
        verify(goodbyeMailUtil, never()).sendEng(any());
        verify(newsletterRepository, never()).save(any());
    }
}
