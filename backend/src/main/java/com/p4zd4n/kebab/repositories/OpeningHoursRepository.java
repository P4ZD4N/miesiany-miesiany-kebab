package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.OpeningHour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningHoursRepository extends JpaRepository<OpeningHour, Long> {
}
