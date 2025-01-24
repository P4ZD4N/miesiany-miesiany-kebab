package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.Size;
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
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
public class MealPromotion extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private Size size;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @ManyToMany(mappedBy = "promotions")
    private List<Meal> meals = new ArrayList<>();

    @Builder
    public MealPromotion(String description, Size size, BigDecimal discountPercentage) {
        this.description = description;
        this.size = size;
        this.discountPercentage = discountPercentage;
    }
}
