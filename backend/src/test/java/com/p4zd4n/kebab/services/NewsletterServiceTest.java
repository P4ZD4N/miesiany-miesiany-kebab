package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.NewsletterSubscriber;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.repositories.NewsletterRepository;
import com.p4zd4n.kebab.responses.newsletter.NewsletterSubscriberResponse;
import com.p4zd4n.kebab.services.newsletter.NewsletterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class NewsletterServiceTest {

    @Mock
    private NewsletterRepository newsletterRepository;

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
}
