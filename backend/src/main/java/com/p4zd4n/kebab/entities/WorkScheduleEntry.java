package com.p4zd4n.kebab.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "work_schedule_entries")
@Getter
@Setter
@NoArgsConstructor
public class WorkScheduleEntry extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "employee_first_name", nullable = false)
    private String employeeFirstName;

    @Column(name = "employee_last_name", nullable = false)
    private String employeeLastName;

    @Column(name = "employee_email", nullable = false)
    private String employeeEmail;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "hourly_wage", nullable = false)
    private BigDecimal hourlyWage;

    @Builder
    public WorkScheduleEntry(
        String employeeFirstName,
        String employeeLastName,
        String employeeEmail,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal hourlyWage
    ) {
        this.employeeFirstName = employeeFirstName;
        this.employeeLastName = employeeLastName;
        this.employeeEmail = employeeEmail;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hourlyWage = hourlyWage;
    }
}
