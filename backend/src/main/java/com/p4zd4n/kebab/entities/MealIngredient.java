package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meals_ingredients")
@Getter
@Setter
@NoArgsConstructor
public class MealIngredient extends WithTimestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "meal_id")
  @JsonIgnore
  private Meal meal;

  @ManyToOne
  @JoinColumn(name = "ingredient_id")
  private Ingredient ingredient;
}
