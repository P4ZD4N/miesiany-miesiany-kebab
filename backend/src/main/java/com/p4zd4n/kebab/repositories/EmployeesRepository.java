package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeesRepository extends JpaRepository<Employee, Long> {
  Optional<Employee> findByEmail(String email);
}
