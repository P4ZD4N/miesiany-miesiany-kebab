package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.utils.DiscountCodeUtil;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "discount_codes")
@Getter
@Setter
@NoArgsConstructor
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Builder
    public DiscountCode(BigDecimal discountPercentage) {
        this.code = DiscountCodeUtil.generateDiscountCode();
        this.discountPercentage = discountPercentage;
        this.expirationDate = LocalDate.now().plusMonths(1);
    }

    @Builder
    public DiscountCode(String code, BigDecimal discountPercentage) {
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.expirationDate = LocalDate.now().plusMonths(1);
    }

    @Builder
    public DiscountCode(BigDecimal discountPercentage, LocalDate expirationDate) {
        this.code = DiscountCodeUtil.generateDiscountCode();
        this.discountPercentage = discountPercentage;
        this.expirationDate = expirationDate;
    }

    @Builder
    public DiscountCode(String code, BigDecimal discountPercentage, LocalDate expirationDate) {
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.expirationDate = expirationDate;
    }
}
