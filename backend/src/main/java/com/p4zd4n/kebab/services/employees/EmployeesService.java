package com.p4zd4n.kebab.services.employees;

import com.p4zd4n.kebab.entities.*;
import com.p4zd4n.kebab.exceptions.alreadyexists.EmployeeAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.notfound.EmployeeNotFoundException;
import com.p4zd4n.kebab.exceptions.others.ManagerDeletionNotAllowedException;
import com.p4zd4n.kebab.exceptions.others.ManagerDemotionNotAllowedException;
import com.p4zd4n.kebab.exceptions.others.ManagerPromotionNotAllowedException;
import com.p4zd4n.kebab.repositories.EmployeesRepository;
import com.p4zd4n.kebab.requests.employee.EmailUpdateRequest;
import com.p4zd4n.kebab.requests.employee.NewEmployeeRequest;
import com.p4zd4n.kebab.requests.employee.UpdatedCredentialsRequest;
import com.p4zd4n.kebab.requests.employee.UpdatedEmployeeRequest;
import com.p4zd4n.kebab.responses.employee.*;
import com.p4zd4n.kebab.utils.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    public EmployeeResponse getCurrentEmployee() {

        log.info("Started retrieving current employee");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();

        Employee employee = employeeRepository.findByEmail(authenticatedEmail)
                        .orElseThrow(() -> new EmployeeNotFoundException(authenticatedEmail));

        log.info("Successfully retrieved current employee");

        return mapToResponse(employee);
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
                .password(PasswordEncoder.encodePassword(request.password()))
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

    public Employee findEmployeeByEmail(String email) {

        log.info("Started finding employee with email '{}'", email);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));

        log.info("Successfully found employee with email '{}'", email);

        return employee;
    }

    public UpdatedEmployeeResponse updateEmployee(Employee employee, UpdatedEmployeeRequest request) {

        UpdatedEmployeeResponse response = UpdatedEmployeeResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated employee with email '" + request.employeeEmail() + "'")
                .build();

        if (request.updatedFirstName() != null) employee.setFirstName(request.updatedFirstName());
        if (request.updatedLastName() != null) employee.setLastName(request.updatedLastName());
        if (request.updatedEmail() != null) updateEmployeeEmail(employee, request);
        if (request.updatedPassword() != null) employee.setPassword(PasswordEncoder.encodePassword(request.updatedPassword()));
        if (request.updatedPhone() != null) employee.setPhoneNumber(request.updatedPhone());
        if (request.updatedJob() != null) updateEmployeeJob(employee, request);
        if (request.updatedHourlyWage() != null) employee.setHourlyWage(request.updatedHourlyWage());
        if (request.updatedDateOfBirth() != null) employee.setDateOfBirth(request.updatedDateOfBirth());
        if (request.updatedEmploymentType() != null) employee.setEmploymentType(request.updatedEmploymentType());
        if (request.updatedActive() != null) employee.setActive(request.updatedActive());
        if (request.updatedHiredDate() != null) employee.setHiredAt(request.updatedHiredDate());

        employeeRepository.save(employee);

        return response;
    }

    private void updateEmployeeEmail(Employee employee, EmailUpdateRequest request) {
        Optional<Employee> existingEmployee = employeeRepository.findByEmail(request.updatedEmail());

        if (existingEmployee.isPresent() && !existingEmployee.get().getId().equals(employee.getId())) {
            throw new EmployeeAlreadyExistsException(request.updatedEmail());
        }

        employee.setEmail(request.updatedEmail());
    }

    private void updateEmployeeJob(Employee employee, UpdatedEmployeeRequest request) {
        if (employee.getJob().equalsIgnoreCase("manager") && !request.updatedJob().equalsIgnoreCase("manager")) {
            throw new ManagerDemotionNotAllowedException();
        }

        if (!employee.getJob().equalsIgnoreCase("manager") && request.updatedJob().equalsIgnoreCase("manager")) {
            throw new ManagerPromotionNotAllowedException();
        }

        employee.setJob(request.updatedJob());
    }

    public UpdatedCredentialsResponse updateCurrentEmployeeCredentials(UpdatedCredentialsRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();

        Employee employee = employeeRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new EmployeeNotFoundException(authenticatedEmail));

        UpdatedCredentialsResponse response = UpdatedCredentialsResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated current employee (" + authenticatedEmail + ") credentials")
                .build();

        if (request.updatedEmail() != null) updateEmployeeEmail(employee, request);
        if (request.updatedPassword() != null) employee.setPassword(PasswordEncoder.encodePassword(request.updatedPassword()));

        employeeRepository.save(employee);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                employee.getEmail(),
                authentication.getCredentials(),
                authentication.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return response;
    }

    public RemovedEmployeeResponse removeEmployee(Employee employee) {
        log.info("Started removing employee with employee '{}'", employee.getEmail());

        if (employee.getJob().equalsIgnoreCase("manager")) {
            throw new ManagerDeletionNotAllowedException();
        }

        employeeRepository.delete(employee);

        RemovedEmployeeResponse response = RemovedEmployeeResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed employee with email '" + employee.getEmail() + "'")
                .build();

        log.info("Successfully removed employee with employee '{}'", employee.getEmail());

        return response;
    }
}
