package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.utils.DiscountCodeUtil;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @Column(name = "remaining_uses", nullable = false)
  private Long remainingUses;

  @Builder
  public DiscountCode(BigDecimal discountPercentage, LocalDate expirationDate, Long remainingUses) {
    this.code = DiscountCodeUtil.generateDiscountCode();
    this.discountPercentage = discountPercentage;
    this.expirationDate = expirationDate;
    this.remainingUses = remainingUses;
  }

  @Builder
  public DiscountCode(
      String code, BigDecimal discountPercentage, LocalDate expirationDate, Long remainingUses) {
    this.code = code;
    this.discountPercentage = discountPercentage;
    this.expirationDate = expirationDate;
    this.remainingUses = remainingUses;
  }
}
