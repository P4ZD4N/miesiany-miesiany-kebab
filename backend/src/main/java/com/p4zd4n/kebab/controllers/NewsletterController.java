package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.exceptions.invalid.InvalidAcceptLanguageHeaderValue;
import com.p4zd4n.kebab.requests.newsletter.NewNewsletterSubscriberRequest;
import com.p4zd4n.kebab.responses.newsletter.NewNewsletterSubscriberResponse;
import com.p4zd4n.kebab.responses.newsletter.NewsletterSubscriberResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.services.newsletter.NewsletterService;
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
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received subscribe newsletter request");

        NewNewsletterSubscriberResponse response = newsletterService.addNewsletterSubscriber(request);

        log.info("Successfully added new newsletter subscriber");

        return ResponseEntity.ok(response);
    }
}
