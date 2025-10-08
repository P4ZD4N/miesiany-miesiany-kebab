package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.WorkScheduleEntry;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkScheduleEntryRepository extends JpaRepository<WorkScheduleEntry, Long> {
  List<WorkScheduleEntry> findByEmployeeEmailAndDate(String employeeEmail, LocalDate date);
}
