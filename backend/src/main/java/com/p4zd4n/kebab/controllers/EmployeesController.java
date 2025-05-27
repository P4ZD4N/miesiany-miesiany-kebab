package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.responses.employee.EmployeeResponse;
import com.p4zd4n.kebab.services.employees.EmployeesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
