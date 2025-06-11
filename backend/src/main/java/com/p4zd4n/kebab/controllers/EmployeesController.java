package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.requests.employee.NewEmployeeRequest;
import com.p4zd4n.kebab.requests.jobs.NewJobOfferRequest;
import com.p4zd4n.kebab.responses.employee.EmployeeResponse;
import com.p4zd4n.kebab.responses.employee.NewEmployeeResponse;
import com.p4zd4n.kebab.responses.jobs.NewJobOfferResponse;
import com.p4zd4n.kebab.services.employees.EmployeesService;
import com.p4zd4n.kebab.utils.LanguageValidator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class EmployeesController {

    private final EmployeesService employeesService;

    public EmployeesController(EmployeesService employeesService) {
        this.employeesService = employeesService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeResponse>> getEmployees() {
        log.info("Received get employees request");
        return ResponseEntity.ok(employeesService.getEmployees());
    }

    @PostMapping("/add-employee")
    public ResponseEntity<NewEmployeeResponse> addEmployee(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody NewEmployeeRequest request
    ) {
        LanguageValidator.validateLanguage(language);

        log.info("Received add employee request");

        NewEmployeeResponse response = employeesService.addEmployee(request);

        log.info("Successfully added new employee with email: {}", request.email());

        return ResponseEntity.ok(response);
    }
}
