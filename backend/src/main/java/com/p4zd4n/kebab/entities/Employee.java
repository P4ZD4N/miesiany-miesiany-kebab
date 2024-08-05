package com.p4zd4n.kebab.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "employee_type")
@Getter
@Setter
@NoArgsConstructor
public class Employee extends WithTimestamp{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "monthly_salary", nullable = false)
    private BigDecimal monthlySalary;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "hired_at", nullable = false)
    private LocalDate hiredAt;

    @Builder
    public Employee(
        String firstName,
        String lastName,
        String email,
        String password,
        LocalDate dateOfBirth,
        String phoneNumber,
        BigDecimal monthlySalary,
        boolean isActive,
        LocalDate hiredAt
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.monthlySalary = monthlySalary;
        this.isActive = isActive;
        this.hiredAt = hiredAt;
    }
}
