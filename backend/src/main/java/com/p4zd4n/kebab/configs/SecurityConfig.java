package com.p4zd4n.kebab.configs;

import com.p4zd4n.kebab.services.auth.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers(
                                "/api/v1/hours/update-opening-hour",
                                "/api/v1/menu/update-beverage",
                                "/api/v1/menu/remove-beverage",
                                "/api/v1/menu/add-beverage",
                                "/api/v1/menu/add-addon",
                                "/api/v1/menu/update-addon",
                                "/api/v1/menu/remove-addon",
                                "/api/v1/menu/add-meal",
                                "/api/v1/menu/update-meal",
                                "/api/v1/menu/remove-meal",
                                "/api/v1/menu/add-ingredient",
                                "/api/v1/menu/remove-ingredient",
                                "/api/v1/contact/update-contact",
                                "/api/v1/jobs/job-offers/manager",
                                "/api/v1/jobs/add-job-offer",
                                "/api/v1/jobs/update-job-offer",
                                "/api/v1/jobs/remove-job-offer",
                                "/api/v1/jobs/download-cv/**",
                                "/api/v1/jobs/remove-job-application",
                                "/api/v1/promotions/add-meal-promotion",
                                "/api/v1/promotions/update-meal-promotion",
                                "/api/v1/promotions/remove-meal-promotion",
                                "/api/v1/promotions/add-beverage-promotion",
                                "/api/v1/promotions/update-meal-promotion",
                                "/api/v1/promotions/remove-beverage-promotion").hasRole("MANAGER")
                        .requestMatchers("api/v1/auth/logout").hasAnyRole("MANAGER", "EMPLOYEE")
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }
}