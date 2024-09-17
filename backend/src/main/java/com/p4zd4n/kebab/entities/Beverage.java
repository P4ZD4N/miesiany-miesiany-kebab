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

    @Column(name = "beverage_name", nullable = false)
    private String beverageName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    public Beverage(String beverageName, BigDecimal price) {
        this.beverageName = beverageName;
        this.price = price;
    }
}
