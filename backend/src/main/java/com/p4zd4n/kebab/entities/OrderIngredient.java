package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_ingredients")
@Getter
@Setter
@NoArgsConstructor
public class OrderIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder
    public OrderIngredient(Order order, Ingredient ingredient, Integer quantity) {
        this.order = order;
        this.ingredient = ingredient;
        this.quantity = quantity;
    }
}
