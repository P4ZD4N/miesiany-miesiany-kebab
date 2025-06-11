package com.p4zd4n.kebab.services.employees;

import com.p4zd4n.kebab.entities.DiscountCode;
import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.entities.JobApplication;
import com.p4zd4n.kebab.entities.Manager;
import com.p4zd4n.kebab.exceptions.alreadyexists.DiscountCodeAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.alreadyexists.EmployeeAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.notfound.JobOfferNotFoundException;
import com.p4zd4n.kebab.repositories.EmployeesRepository;
import com.p4zd4n.kebab.requests.discountcodes.NewDiscountCodeRequest;
import com.p4zd4n.kebab.requests.employee.NewEmployeeRequest;
import com.p4zd4n.kebab.responses.discountcodes.DiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.NewDiscountCodeResponse;
import com.p4zd4n.kebab.responses.employee.EmployeeResponse;
import com.p4zd4n.kebab.responses.employee.NewEmployeeResponse;
import com.p4zd4n.kebab.responses.jobs.JobOfferApplicationResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.Managed;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    public NewEmployeeResponse addEmployee(NewEmployeeRequest request) {

        log.info("Started adding employee with email '{}'", request.email());

        if (employeeRepository.findByEmail(request.email()).isPresent()) {
            throw new EmployeeAlreadyExistsException(request.email());
        }

        if (request.job().equalsIgnoreCase("manager")) {

            Manager newManager = new Manager(
                    request.firstName(),
                    request.lastName(),
                    request.email(),
                    request.password(),
                    request.dateOfBirth(),
                    request.phone(),
                    request.hourlyWage(),
                    true,
                    LocalDate.now());

            employeeRepository.save(newManager);

            return NewEmployeeResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Successfully added new manager with email '" + request.email() + "'")
                    .build();
        }

        Employee newEmployee = Employee.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(request.password())
                .phoneNumber(request.phone())
                .dateOfBirth(request.dateOfBirth())
                .job(request.job())
                .employmentType(request.employmentType())
                .hourlyWage(request.hourlyWage())
                .isActive(true)
                .hiredAt(LocalDate.now())
                .build();

        employeeRepository.save(newEmployee);

        return NewEmployeeResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new employee with email '" + request.email() + "'")
                .build();
    }
}
