package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.requests.employee.NewEmployeeRequest;
import com.p4zd4n.kebab.requests.employee.RemovedEmployeeRequest;
import com.p4zd4n.kebab.requests.employee.UpdatedEmployeeRequest;
import com.p4zd4n.kebab.responses.employee.EmployeeResponse;
import com.p4zd4n.kebab.responses.employee.NewEmployeeResponse;
import com.p4zd4n.kebab.responses.employee.RemovedEmployeeResponse;
import com.p4zd4n.kebab.responses.employee.UpdatedEmployeeResponse;
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

    @PutMapping("/update-employee")
    public ResponseEntity<UpdatedEmployeeResponse> updateEmployee(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody UpdatedEmployeeRequest request
    ) {
        LanguageValidator.validateLanguage(language);

        log.info("Received update job employee request");

        Employee existingEmployee = employeesService.findEmployeeByEmail(request.employeeEmail());
        UpdatedEmployeeResponse response = employeesService.updateEmployee(existingEmployee, request);

        log.info("Successfully updated employee with email: {}", request.employeeEmail());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-employee")
    public ResponseEntity<RemovedEmployeeResponse> removeOrder(
            @Valid @RequestBody RemovedEmployeeRequest request
    ) {
        log.info("Received remove employee request");

        Employee existingEmployee = employeesService.findEmployeeByEmail(request.email());
        RemovedEmployeeResponse response = employeesService.removeEmployee(existingEmployee);

        return ResponseEntity.ok(response);
    }
}
