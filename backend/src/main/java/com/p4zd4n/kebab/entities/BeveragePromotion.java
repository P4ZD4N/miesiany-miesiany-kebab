package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.Size;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "beverages_promotions")
@Getter
@Setter
@NoArgsConstructor
public class BeveragePromotion extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @OneToMany(mappedBy = "beveragePromotion")
    private List<Beverage> beverages = new ArrayList<>();

    @Builder
    public BeveragePromotion(String description, BigDecimal discountPercentage) {
        this.description = description;
        this.discountPercentage = discountPercentage;
    }
}
