package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Addon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddonRepository extends JpaRepository<Addon, Long> {
    Optional<Addon> findByName(String name);
}
