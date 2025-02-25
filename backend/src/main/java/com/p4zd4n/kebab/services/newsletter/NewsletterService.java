package com.p4zd4n.kebab.services.newsletter;

import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.entities.NewsletterSubscriber;
import com.p4zd4n.kebab.exceptions.alreadyexists.MealAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.alreadyexists.SubscriberAlreadyExistsException;
import com.p4zd4n.kebab.repositories.NewsletterRepository;
import com.p4zd4n.kebab.requests.newsletter.NewNewsletterSubscriberRequest;
import com.p4zd4n.kebab.responses.newsletter.NewNewsletterSubscriberResponse;
import com.p4zd4n.kebab.responses.newsletter.NewsletterSubscriberResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;

    public NewsletterService(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
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
                .subscribedAt(newsletterSubscriber.getCreatedAt())
                .isActive(newsletterSubscriber.isActive())
                .build();
    }

    public NewNewsletterSubscriberResponse addNewsletterSubscriber(NewNewsletterSubscriberRequest request) {

        Optional<NewsletterSubscriber> subscriber = newsletterRepository.findByEmail(request.email());

        if (subscriber.isPresent()) throw new SubscriberAlreadyExistsException(request.email());

        NewsletterSubscriber newSubscriber = NewsletterSubscriber.builder()
                .email(request.email())
                .subscriberFirstName(request.firstName())
                .isActive(false)
                .build();

        newsletterRepository.save(newSubscriber);

        return NewNewsletterSubscriberResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new newsletter subscriber with email '" + request.email() + "'")
                .build();
    }
}
