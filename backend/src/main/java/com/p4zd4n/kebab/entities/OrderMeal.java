package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.p4zd4n.kebab.enums.Size;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_meals")
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderMeal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  @JsonIgnore
  private Order order;

  @Column(name = "meal_name", nullable = false)
  private String mealName;

  @Column(name = "final_price", nullable = false)
  private BigDecimal finalPrice;

  @Column(name = "size", nullable = false)
  private Size size;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "order_meal_ingredients",
      joinColumns = @JoinColumn(name = "order_meal_id"))
  @Column(name = "ingredient_name")
  private Set<String> ingredientNames = new HashSet<>();

  @Builder
  public OrderMeal(
      Order order,
      String mealName,
      BigDecimal finalPrice,
      Size size,
      Integer quantity,
      Set<String> ingredientNames) {
    this.order = order;
    this.mealName = mealName;
    this.finalPrice = finalPrice;
    this.size = size;
    this.quantity = quantity;
    this.ingredientNames = ingredientNames;
  }
}
