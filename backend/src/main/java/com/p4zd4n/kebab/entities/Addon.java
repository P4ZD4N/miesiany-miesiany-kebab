package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToMany(mappedBy = "addons")
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private AddonPromotion promotion;

    @Builder
    public Addon(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }
}
