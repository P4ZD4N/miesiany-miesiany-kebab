package com.p4zd4n.kebab.services.employees;

import com.p4zd4n.kebab.entities.DiscountCode;
import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.repositories.EmployeesRepository;
import com.p4zd4n.kebab.responses.discountcodes.DiscountCodeResponse;
import com.p4zd4n.kebab.responses.employee.EmployeeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeesService {

    private final EmployeesRepository employeeRepository;

    public EmployeesService(EmployeesRepository employeesRepository) {
        this.employeeRepository = employeesRepository;
    }

    public List<EmployeeResponse> getEmployees() {

        log.info("Started retrieving employees");

        List<Employee> employees = employeeRepository.findAll();

        List<EmployeeResponse> response = employees.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved employees");

        return response;
    }

    public EmployeeResponse mapToResponse(Employee employee) {

        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .dateOfBirth(employee.getDateOfBirth())
                .job(employee.getJob())
                .employmentType(employee.getEmploymentType())
                .hourlyWage(employee.getHourlyWage())
                .isActive(employee.isActive())
                .hiredAt(employee.getHiredAt())
                .build();
    }
}
