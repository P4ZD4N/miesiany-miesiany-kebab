package com.p4zd4n.kebab;

import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.repositories.EmployeeRepository;
import com.p4zd4n.kebab.utils.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
@AllArgsConstructor
public class KebabApplication implements CommandLineRunner {

	private EmployeeRepository employeeRepository;

	public static void main(String[] args) {
		SpringApplication.run(KebabApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Employee employee = Employee.builder()
				.firstName("Admin")
				.lastName("User")
				.email("admin@example.com")
				.password(PasswordEncoder.encodePassword("admin123"))
				.dateOfBirth(LocalDate.of(1990, 1, 1))
				.phoneNumber("123456789")
				.monthlySalary(BigDecimal.valueOf(3000))
				.isActive(true)
				.hiredAt(LocalDate.now())
				.build();

		employeeRepository.save(employee);
	}
}
