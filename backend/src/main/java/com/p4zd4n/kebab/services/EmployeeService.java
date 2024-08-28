package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.repositories.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Optional<Employee> findByEmail(String email) {

        log.info("Searching for employee with email '{}'", email);

        Optional<Employee> employee = employeeRepository.findByEmail(email);

        if (employee.isEmpty()) log.error("Employee not found with email: {}", email);
        else log.info("Employee found with email: {}", email);

        return employeeRepository.findByEmail(email);
    }
}
