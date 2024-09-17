package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeverageRepository extends JpaRepository<Beverage, Long> {
}
