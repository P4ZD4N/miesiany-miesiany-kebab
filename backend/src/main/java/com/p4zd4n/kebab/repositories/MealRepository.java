package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Meal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MealRepository extends JpaRepository<Meal, Long> {

    @Query("SELECT m FROM Meal m WHERE LOWER(m.name) = LOWER(:name)")
    Optional<Meal> findByName(@Param("name") String name);

    List<Meal> findAllByNameIn(Set<String> names);
}
