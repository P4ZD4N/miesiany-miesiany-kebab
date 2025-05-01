package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.p4zd4n.kebab.enums.Size;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "order_meals")
@Getter
@Setter
@NoArgsConstructor
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
            joinColumns = @JoinColumn(name = "order_meal_id")
    )
    @Column(name = "ingredient_name")
    private Set<String> ingredientNames = new HashSet<>();

    @Builder
    public OrderMeal(Order order, Meal meal, Size size, Integer quantity) {
        this.order = order;
        this.mealName = meal.getName();

        BigDecimal basePrice = meal.getPriceForSize(size);
        BigDecimal discount = meal.getPromotions().stream()
                .filter(promotion -> promotion.getSizes().contains(size))
                .map(MealPromotion::getDiscountPercentage)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal discountFraction = discount.divide(BigDecimal.valueOf(100));
        BigDecimal discountedPrice = basePrice.subtract(basePrice.multiply(discountFraction));

        this.finalPrice = discountedPrice.multiply(BigDecimal.valueOf(quantity));
        this.size = size;
        this.quantity = quantity;
        this.ingredientNames = meal.getMealIngredients().stream()
                .map(mealIngredient -> mealIngredient.getIngredient().getName())
                .collect(Collectors.toSet());
    }
}
