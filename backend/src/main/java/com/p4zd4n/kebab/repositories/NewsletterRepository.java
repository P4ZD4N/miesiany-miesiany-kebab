package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterRepository extends JpaRepository<NewsletterSubscriber, Long> {
}
