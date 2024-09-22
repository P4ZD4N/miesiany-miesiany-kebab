package com.p4zd4n.kebab.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "beverages")
@Getter
@Setter
@NoArgsConstructor
public class Beverage extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "capacity", nullable = false)
    private BigDecimal capacity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    public Beverage(String name, BigDecimal price, BigDecimal capacity) {
        this.name = name;
        this.price = price;
        this.capacity = capacity;
    }
}
