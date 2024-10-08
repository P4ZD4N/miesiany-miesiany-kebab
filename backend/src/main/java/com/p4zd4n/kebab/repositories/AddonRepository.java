package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Addon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AddonRepository extends JpaRepository<Addon, Long> {

    @Query("SELECT a FROM Addon a WHERE LOWER(a.name) = LOWER(:name)")
    Optional<Addon> findByName(@Param("name") String name);
}
