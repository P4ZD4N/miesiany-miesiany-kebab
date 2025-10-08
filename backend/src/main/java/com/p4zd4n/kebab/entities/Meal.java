package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.IngredientType;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.exceptions.others.ExcessBreadException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meals")
@Getter
@Setter
@NoArgsConstructor
public class Meal extends WithTimestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "meal_promotions",
      joinColumns = @JoinColumn(name = "meal_id"),
      inverseJoinColumns = @JoinColumn(name = "promotion_id"))
  private List<MealPromotion> promotions = new ArrayList<>();

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "meal_prices", joinColumns = @JoinColumn(name = "meal_id"))
  @MapKeyColumn(name = "size")
  @Column(name = "price")
  @Enumerated(EnumType.STRING)
  private Map<Size, BigDecimal> prices = new EnumMap<>(Size.class);

  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "meal",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<MealIngredient> mealIngredients = new ArrayList<>();

  @Builder
  public Meal(String name, Map<Size, BigDecimal> prices) {
    this.name = name;
    this.prices = prices;
  }

  public void addIngredient(Ingredient ingredient) {

    if (ingredient.getIngredientType().equals(IngredientType.BREAD) && hasBread()) {
      throw new ExcessBreadException();
    }

    MealIngredient mealIngredient = new MealIngredient();

    mealIngredient.setMeal(this);
    mealIngredient.setIngredient(ingredient);

    this.mealIngredients.add(mealIngredient);
  }

  public void removeIngredient(Ingredient ingredient) {

    MealIngredient mealIngredientToRemove =
        this.mealIngredients.stream()
            .filter(mealIngredient -> mealIngredient.getIngredient().equals(ingredient))
            .findFirst()
            .orElse(null);

    this.mealIngredients.remove(mealIngredientToRemove);
  }

  private boolean hasBread() {

    return mealIngredients.stream()
        .map(MealIngredient::getIngredient)
        .anyMatch(i -> i.getIngredientType().equals(IngredientType.BREAD));
  }

  public BigDecimal getPriceForSizeWithDiscountIncluded(Size size, Integer quantity) {
    BigDecimal basePrice = getPriceForSize(size);
    BigDecimal discount =
        promotions.stream()
            .filter(promotion -> promotion.getSizes().contains(size))
            .map(MealPromotion::getDiscountPercentage)
            .findFirst()
            .orElse(BigDecimal.ZERO);

    BigDecimal discountFraction = discount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    BigDecimal discountedPrice = basePrice.subtract(basePrice.multiply(discountFraction));

    return discountedPrice.multiply(BigDecimal.valueOf(quantity));
  }

  public BigDecimal getPriceForSize(Size size) {
    return prices.get(size);
  }

  public void setPriceForSize(Size size, BigDecimal price) {
    prices.put(size, price);
  }
}
