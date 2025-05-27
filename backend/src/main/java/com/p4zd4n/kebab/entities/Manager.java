package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.EmploymentType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("Manager")
@NoArgsConstructor
public class Manager extends Employee {

    public Manager(
        String firstName,
        String lastName,
        String email,
        String password,
        LocalDate dateOfBirth,
        String phoneNumber,
        BigDecimal hourlyWage,
        boolean isActive,
        LocalDate hiredAt
    ) {
        super(firstName, lastName, email, password, dateOfBirth, phoneNumber, "Manager", EmploymentType.PERMANENT, hourlyWage, isActive, hiredAt);
    }
}
