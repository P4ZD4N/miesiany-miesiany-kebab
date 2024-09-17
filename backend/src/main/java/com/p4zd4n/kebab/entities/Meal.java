package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.Size;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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

    @Column(name = "meal_name", nullable = false)
    private String mealName;

    @Column(name = "size", nullable = false)
    private Size size;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "meal")
    private List<MealIngredient> mealIngredients;

    @Builder
    public Meal(String mealName, BigDecimal price) {
        this.mealName = mealName;
        this.price = price;
    }
}
