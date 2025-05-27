package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.requests.newsletter.NewNewsletterSubscriberRequest;
import com.p4zd4n.kebab.requests.newsletter.RegenerateOtpRequest;
import com.p4zd4n.kebab.requests.newsletter.UnsubscribeRequest;
import com.p4zd4n.kebab.requests.newsletter.VerifyNewsletterSubscriptionRequest;
import com.p4zd4n.kebab.responses.newsletter.*;
import com.p4zd4n.kebab.services.newsletter.NewsletterService;
import com.p4zd4n.kebab.utils.LanguageValidator;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/newsletter")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @GetMapping("/subscribers")
    public ResponseEntity<List<NewsletterSubscriberResponse>> getSubscribers() {
        log.info("Received get subscribers request");
        return ResponseEntity.ok(newsletterService.getSubscribers());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<NewNewsletterSubscriberResponse> subscribe(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody NewNewsletterSubscriberRequest request
    ) throws MessagingException {
        LanguageValidator.validateLanguage(language);

        log.info("Received subscribe newsletter request");

        NewNewsletterSubscriberResponse response = newsletterService.addNewsletterSubscriber(request);

        log.info("Successfully added new newsletter subscriber");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/verify-subscription")
    public ResponseEntity<VerifyNewsletterSubscriptionResponse> verifySubscription(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody VerifyNewsletterSubscriptionRequest request
    ) throws MessagingException {
        LanguageValidator.validateLanguage(language);

        log.info("Received verify subscription request");

        VerifyNewsletterSubscriptionResponse response = newsletterService.verifySubscription(request);

        log.info("Successfully verified newsletter subscriber");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/regenerate-otp")
    public ResponseEntity<RegenerateOtpResponse> regenerateOtp(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody RegenerateOtpRequest request
    ) throws MessagingException {
        LanguageValidator.validateLanguage(language);

        log.info("Received regenerate otp request");

        RegenerateOtpResponse response = newsletterService.regenerateOtp(request);

        log.info("Successfully regenerated otp");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<UnsubscribeResponse> unsubscribe(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody UnsubscribeRequest request
    ) throws MessagingException {
        LanguageValidator.validateLanguage(language);

        log.info("Received unsubscribe request from user with email '{}'", request.email());

        UnsubscribeResponse response = newsletterService.unsubscribe(request);

        log.info("User with '{}' email successfully unsubscribed newsletter", request.email());

        return ResponseEntity.ok(response);
    }
}
