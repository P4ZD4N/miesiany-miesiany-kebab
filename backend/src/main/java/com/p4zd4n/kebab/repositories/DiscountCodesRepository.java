package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountCodesRepository extends JpaRepository<DiscountCode, Long> {
    Optional<DiscountCode> findByCode(String code);
}
