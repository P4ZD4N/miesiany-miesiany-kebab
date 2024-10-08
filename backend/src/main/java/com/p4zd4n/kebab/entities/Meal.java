package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.IngredientType;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.exceptions.others.ExcessBreadException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ElementCollection
    @CollectionTable(name = "meal_prices", joinColumns = @JoinColumn(name = "meal_id"))
    @MapKeyColumn(name = "size")
    @Column(name = "price")
    @Enumerated(EnumType.STRING)
    private Map<Size, BigDecimal> prices = new EnumMap<>(Size.class);

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
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

        MealIngredient mealIngredientToRemove = this.mealIngredients.stream()
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

    public BigDecimal getPriceForSize(Size size) {
        return prices.get(size);
    }

    public void setPriceForSize(Size size, BigDecimal price) {
        prices.put(size, price);
    }
}
