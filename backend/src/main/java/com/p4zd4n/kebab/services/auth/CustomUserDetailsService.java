package com.p4zd4n.kebab.services.auth;

import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.entities.Manager;
import com.p4zd4n.kebab.exceptions.EmployeeNotFoundException;
import com.p4zd4n.kebab.repositories.EmployeeRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));

        GrantedAuthority authority;
        if (employee instanceof Manager) {
            authority = new SimpleGrantedAuthority("ROLE_MANAGER");
        } else {
            authority = new SimpleGrantedAuthority("ROLE_EMPLOYEE");
        }

        return new User(
                employee.getEmail(),
                employee.getPassword(),
                Collections.singletonList(authority)
        );
    }
}