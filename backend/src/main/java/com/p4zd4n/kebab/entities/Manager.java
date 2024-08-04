package com.p4zd4n.kebab.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "managers")
@NoArgsConstructor
public class Manager extends Employee {

    @Builder
    public Manager(String firstName,
                   String lastName,
                   String email,
                   String password,
                   LocalDate dateOfBirth,
                   String phoneNumber,
                   BigDecimal monthlySalary,
                   boolean isActive,
                   LocalDate hiredAt) {
        super(firstName, lastName, email, password, dateOfBirth, phoneNumber, monthlySalary, isActive, hiredAt);
    }
}
