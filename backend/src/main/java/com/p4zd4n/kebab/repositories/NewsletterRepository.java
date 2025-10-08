package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.NewsletterSubscriber;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterRepository extends JpaRepository<NewsletterSubscriber, Long> {
  Optional<NewsletterSubscriber> findByEmail(String email);

  Optional<NewsletterSubscriber> findByOtp(Integer otp);

  List<NewsletterSubscriber> findAllByIsActiveTrue();
}
