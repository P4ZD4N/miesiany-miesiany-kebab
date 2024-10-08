package com.p4zd4n.kebab;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AllArgsConstructor
public class KebabApplication {
	public static void main(String[] args) {
		SpringApplication.run(KebabApplication.class, args);
	}
}
