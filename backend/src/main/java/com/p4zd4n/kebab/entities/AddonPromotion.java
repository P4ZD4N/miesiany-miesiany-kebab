package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addons_promotions")
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddonPromotion extends WithTimestamp implements Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @OneToMany(mappedBy = "promotion")
    @JsonIgnore
    private List<Addon> addons = new ArrayList<>();

    @Builder
    public AddonPromotion(String description, BigDecimal discountPercentage) {
        this.description = description;
        this.discountPercentage = discountPercentage;
    }
}
