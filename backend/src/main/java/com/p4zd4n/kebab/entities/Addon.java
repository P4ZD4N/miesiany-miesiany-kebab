package com.p4zd4n.kebab.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "addons")
@Getter
@Setter
@NoArgsConstructor
public class Addon extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "addon_name", nullable = false)
    private String addonName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    public Addon(String addonName, BigDecimal price) {
        this.addonName = addonName;
        this.price = price;
    }
}
