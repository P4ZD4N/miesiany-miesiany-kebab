package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.OpeningHour;
import com.p4zd4n.kebab.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpeningHoursRepository extends JpaRepository<OpeningHour, Long> {

  Optional<OpeningHour> findByDayOfWeek(DayOfWeek dayOfWeek);
}
