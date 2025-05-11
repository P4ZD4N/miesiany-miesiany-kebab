package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.WorkScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkScheduleEntryRepository extends JpaRepository<WorkScheduleEntry, Long> {
    List<WorkScheduleEntry> findByEmployee_EmailAndDate(String email, LocalDate date);
}
