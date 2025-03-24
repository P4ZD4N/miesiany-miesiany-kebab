package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_beverages")
@Getter
@Setter
@NoArgsConstructor
public class OrderBeverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "beverage_id", nullable = false)
    private Beverage beverage;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder
    public OrderBeverage(Order order, Beverage beverage, Integer quantity) {
        this.order = order;
        this.beverage = beverage;
        this.quantity = quantity;
    }
}