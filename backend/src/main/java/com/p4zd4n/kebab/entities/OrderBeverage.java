package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

    @Column(name = "beverage_name", nullable = false)
    private String beverageName;

    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    @Column(name = "capacity", nullable = false)
    private BigDecimal capacity;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder
    public OrderBeverage(
            Order order,
            String beverageName,
            BigDecimal finalPrice,
            BigDecimal capacity,
            Integer quantity
    ) {
        this.order = order;
        this.beverageName = beverageName;
        this.finalPrice = finalPrice;
        this.capacity = capacity;
        this.quantity = quantity;
    }
}