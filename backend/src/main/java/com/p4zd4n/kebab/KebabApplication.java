package com.p4zd4n.kebab;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@AllArgsConstructor
@EnableAsync
public class KebabApplication {
	public static void main(String[] args) {
		SpringApplication.run(KebabApplication.class, args);
	}
}
