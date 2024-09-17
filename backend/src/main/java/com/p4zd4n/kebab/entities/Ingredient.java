package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.IngredientType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
public class Ingredient extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "ingredient_type", nullable = false)
    private IngredientType ingredientType;

    @Builder
    public Ingredient(String name, IngredientType ingredientType) {
        this.name = name;
        this.ingredientType = ingredientType;
    }
}
