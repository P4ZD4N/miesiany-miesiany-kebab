package com.p4zd4n.kebab.services.newsletter;

import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.entities.NewsletterSubscriber;
import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
import com.p4zd4n.kebab.exceptions.alreadyexists.MealAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.alreadyexists.SubscriberAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.expired.OtpExpiredException;
import com.p4zd4n.kebab.exceptions.failed.OtpGenerationFailedException;
import com.p4zd4n.kebab.exceptions.notfound.SubscriberNotFoundException;
import com.p4zd4n.kebab.exceptions.notmatches.OtpNotMatchesException;
import com.p4zd4n.kebab.repositories.NewsletterRepository;
import com.p4zd4n.kebab.requests.newsletter.NewNewsletterSubscriberRequest;
import com.p4zd4n.kebab.requests.newsletter.RegenerateOtpRequest;
import com.p4zd4n.kebab.requests.newsletter.VerifyNewsletterSubscriptionRequest;
import com.p4zd4n.kebab.responses.newsletter.NewNewsletterSubscriberResponse;
import com.p4zd4n.kebab.responses.newsletter.NewsletterSubscriberResponse;
import com.p4zd4n.kebab.responses.newsletter.RegenerateOtpResponse;
import com.p4zd4n.kebab.responses.newsletter.VerifyNewsletterSubscriptionResponse;
import com.p4zd4n.kebab.utils.OtpUtil;
import com.p4zd4n.kebab.utils.VerificationMailUtil;
import com.p4zd4n.kebab.utils.WelcomeMailUtil;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;
    private final OtpUtil otpUtil;
    private final VerificationMailUtil verificationMailUtil;
    private final WelcomeMailUtil welcomeMailUtil;

    public NewsletterService(
            NewsletterRepository newsletterRepository,
            OtpUtil otpUtil,
            VerificationMailUtil verificationMailUtil,
            WelcomeMailUtil welcomeMailUtil
    ) {
        this.newsletterRepository = newsletterRepository;
        this.otpUtil = otpUtil;
        this.verificationMailUtil = verificationMailUtil;
        this.welcomeMailUtil = welcomeMailUtil;
    }

    public List<NewsletterSubscriberResponse> getSubscribers() {

        log.info("Started retrieving subscribers");

        List<NewsletterSubscriber> subscribers = newsletterRepository.findAll();

        List<NewsletterSubscriberResponse> response = subscribers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved subscribers");

        return response;
    }

    public NewsletterSubscriberResponse mapToResponse(NewsletterSubscriber newsletterSubscriber) {

        return NewsletterSubscriberResponse.builder()
                .email(newsletterSubscriber.getEmail())
                .firstName(newsletterSubscriber.getSubscriberFirstName())
                .newsletterMessagesLanguage(newsletterSubscriber.getNewsletterMessagesLanguage())
                .subscribedAt(newsletterSubscriber.getCreatedAt())
                .isActive(newsletterSubscriber.isActive())
                .build();
    }

    public NewNewsletterSubscriberResponse addNewsletterSubscriber(NewNewsletterSubscriberRequest request) throws MessagingException {

        Optional<NewsletterSubscriber> subscriber = newsletterRepository.findByEmail(request.email());

        if (subscriber.isPresent()) throw new SubscriberAlreadyExistsException(request.email());

        Integer otp = otpUtil.generateOtp();

        if (request.newsletterMessagesLanguage().equals(NewsletterMessagesLanguage.ENGLISH))
            verificationMailUtil.sendEng(request.email(), otp);
        else
            verificationMailUtil.sendPl(request.email(), otp);

        NewsletterSubscriber newSubscriber = NewsletterSubscriber.builder()
                .email(request.email())
                .subscriberFirstName(request.firstName())
                .newsletterMessagesLanguage(request.newsletterMessagesLanguage())
                .isActive(false)
                .otp(otp)
                .otpGeneratedTime(LocalDateTime.now())
                .build();

        newsletterRepository.save(newSubscriber);

        return NewNewsletterSubscriberResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new newsletter subscriber with email '" + request.email() + "'")
                .build();
    }

    public VerifyNewsletterSubscriptionResponse verifySubscription(VerifyNewsletterSubscriptionRequest request) throws MessagingException {

        NewsletterSubscriber subscriber = newsletterRepository.findByEmail(request.email())
                .orElseThrow(() -> new SubscriberNotFoundException(request.email()));

        boolean otpMatches = subscriber.getOtp().equals(request.otp());
        boolean otpExpired = !(Duration.between(subscriber.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < OtpUtil.OTP_EXPIRATION_SECONDS);

        if (!otpMatches) throw new OtpNotMatchesException();
        if (otpExpired) throw new OtpExpiredException();

        subscriber.setActive(true);
        newsletterRepository.save(subscriber);

        if (subscriber.getNewsletterMessagesLanguage().equals(NewsletterMessagesLanguage.ENGLISH))
            welcomeMailUtil.sendEng(request.email());
        else
            welcomeMailUtil.sendPl(request.email());

        return VerifyNewsletterSubscriptionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully verified newsletter subscriber with '" + request.email() + "' email!")
                .build();
    }

    public RegenerateOtpResponse regenerateOtp(RegenerateOtpRequest request) throws MessagingException {

        NewsletterSubscriber subscriber = newsletterRepository.findByEmail(request.email())
                .orElseThrow(() -> new SubscriberNotFoundException(request.email()));

        Integer newOtp = generateOtpIfNotExists();

        if (subscriber.getNewsletterMessagesLanguage().equals(NewsletterMessagesLanguage.ENGLISH))
            verificationMailUtil.sendEng(request.email(), newOtp);
        else
            verificationMailUtil.sendPl(request.email(), newOtp);

        subscriber.setOtp(newOtp);
        subscriber.setOtpGeneratedTime(LocalDateTime.now());
        newsletterRepository.save(subscriber);

        return RegenerateOtpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully regenerated otp for subscriber with email '" + request.email() + "'!")
                .build();
    }

    private Integer generateOtpIfNotExists() {

        Integer otp;
        final int MAX_ATTEMPTS = 5;
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            otp = otpUtil.generateOtp();
            if (newsletterRepository.findByOtp(otp).isEmpty()) return otp;
            attempts++;
        }

        throw new OtpGenerationFailedException(MAX_ATTEMPTS);
    }
}
