package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

  Optional<Customer> findCustomerByEmail(String email);
}
