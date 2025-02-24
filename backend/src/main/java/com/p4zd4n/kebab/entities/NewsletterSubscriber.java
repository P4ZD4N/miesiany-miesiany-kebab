package com.p4zd4n.kebab.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "newsletter_subscribers")
@Getter
@Setter
@NoArgsConstructor
public class NewsletterSubscriber extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "subscriber_first_name", nullable = false)
    private String subscriberFirstName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Builder
    public NewsletterSubscriber(String subscriberFirstName, String email, boolean isActive) {
        this.subscriberFirstName = subscriberFirstName;
        this.email = email;
        this.isActive = isActive;
    }
}
