package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BeverageRepository extends JpaRepository<Beverage, Long> {

    Optional<Beverage> findByNameAndCapacity(String name, BigDecimal capacity);
    Optional<List<Beverage>> findByCapacity(BigDecimal capacity);
}
