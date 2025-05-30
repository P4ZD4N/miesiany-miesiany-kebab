package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) = LOWER(:name)")
    Optional<Ingredient> findByName(@Param("name") String name);
    List<Ingredient> findAllByNameIn(Set<String> names);
}
