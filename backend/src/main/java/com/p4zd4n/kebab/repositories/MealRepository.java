package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {
}
