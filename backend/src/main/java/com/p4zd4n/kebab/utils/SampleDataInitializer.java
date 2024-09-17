package com.p4zd4n.kebab.utils;

import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.entities.Manager;
import com.p4zd4n.kebab.entities.OpeningHour;
import com.p4zd4n.kebab.enums.DayOfWeek;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.repositories.EmployeeRepository;
import com.p4zd4n.kebab.repositories.OpeningHoursRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

@Component
public class SampleDataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final BeverageRepository beverageRepository;

    public SampleDataInitializer(
            EmployeeRepository employeeRepository,
            OpeningHoursRepository openingHoursRepository,
            BeverageRepository beverageRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.openingHoursRepository = openingHoursRepository;
        this.beverageRepository = beverageRepository;
    }

    @Override
    public void run(String... args) {

        initOpeningHours();
        initSampleEmployees();
        initSampleManagers();
        initBeverages();
    }

    private void initOpeningHours() {

        if (openingHoursRepository.count() == 0) {
            Arrays.stream(DayOfWeek.values()).forEach(dayOfWeek -> {
                OpeningHour openingHour = OpeningHour.builder()
                        .dayOfWeek(dayOfWeek)
                        .openingTime(LocalTime.of(10, 0))
                        .closingTime(LocalTime.of(22, 0))
                        .build();
                openingHoursRepository.save(openingHour);
            });
        }
    }

    private void initSampleEmployees() {

        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("employee@example.com")
                .password(PasswordEncoder.encodePassword("employee123"))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .monthlySalary(BigDecimal.valueOf(3000))
                .isActive(true)
                .hiredAt(LocalDate.now())
                .build();

        employeeRepository.save(employee);
    }

    private void initSampleManagers() {

        Manager manager = new Manager(
                "Wiktor",
                "Chudy",
                "manager@example.com",
                PasswordEncoder.encodePassword("manager123"),
                LocalDate.of(2003, 8, 1),
                "123456798",
                BigDecimal.valueOf(5000),
                true,
                LocalDate.now()
        );

        employeeRepository.save(manager);
    }

    private void initBeverages() {

        Beverage cocaCola = Beverage.builder()
                .name("Coca-Cola")
                .price(BigDecimal.valueOf(6))
                .capacity(BigDecimal.valueOf(0.33))
                .build();

        Beverage cocaColaZero = Beverage.builder()
                .name("Coca-Cola Zero")
                .price(BigDecimal.valueOf(6))
                .capacity(BigDecimal.valueOf(0.33))
                .build();

        Beverage fanta = Beverage.builder()
                .name("Fanta")
                .price(BigDecimal.valueOf(6))
                .capacity(BigDecimal.valueOf(0.33))
                .build();

        Beverage sprite = Beverage.builder()
                .name("Sprite")
                .price(BigDecimal.valueOf(6))
                .capacity(BigDecimal.valueOf(0.33))
                .build();

        Beverage ayran = Beverage.builder()
                .name("Ayran")
                .price(BigDecimal.valueOf(5))
                .capacity(BigDecimal.valueOf(0.5))
                .build();

        Beverage water = Beverage.builder()
                .name("Water")
                .price(BigDecimal.valueOf(5))
                .capacity(BigDecimal.valueOf(0.5))
                .build();

        beverageRepository.save(cocaCola);
        beverageRepository.save(cocaColaZero);
        beverageRepository.save(fanta);
        beverageRepository.save(sprite);
        beverageRepository.save(ayran);
        beverageRepository.save(water);
    }
}
