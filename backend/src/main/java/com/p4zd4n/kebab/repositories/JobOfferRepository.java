package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
}
