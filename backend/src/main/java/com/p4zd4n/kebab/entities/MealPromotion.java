package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.Size;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

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

    @ElementCollection(targetClass = Size.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "promotion_sizes", joinColumns = @JoinColumn(name = "promotion_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private Set<Size> sizes;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @ManyToMany(mappedBy = "promotions")
    private List<Meal> meals = new ArrayList<>();

    @Builder
    public MealPromotion(String description, Set<Size> sizes, BigDecimal discountPercentage) {
        this.description = description;
        this.sizes = sizes;
        this.discountPercentage = discountPercentage;
    }
}
