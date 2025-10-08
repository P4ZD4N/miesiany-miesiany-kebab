package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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

  @Column(name = "newsletter_language", nullable = false)
  private NewsletterMessagesLanguage newsletterMessagesLanguage;

  @Column(name = "is_active", nullable = false)
  private boolean isActive;

  @Column(name = "otp")
  private Integer otp;

  @Column(name = "otp_generated_time")
  private LocalDateTime otpGeneratedTime;

  @Builder
  public NewsletterSubscriber(
      String subscriberFirstName,
      String email,
      NewsletterMessagesLanguage newsletterMessagesLanguage,
      boolean isActive,
      Integer otp,
      LocalDateTime otpGeneratedTime) {
    this.subscriberFirstName = subscriberFirstName;
    this.email = email;
    this.newsletterMessagesLanguage = newsletterMessagesLanguage;
    this.isActive = isActive;
    this.otp = otp;
    this.otpGeneratedTime = otpGeneratedTime;
  }
}
