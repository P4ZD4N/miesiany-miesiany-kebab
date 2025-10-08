package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_addons")
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderAddon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  @JsonIgnore
  private Order order;

  @Column(name = "addon_name", nullable = false)
  private String addonName;

  @Column(name = "final_price", nullable = false)
  private BigDecimal finalPrice;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Builder
  public OrderAddon(Order order, String addonName, BigDecimal finalPrice, Integer quantity) {
    this.order = order;
    this.addonName = addonName;
    this.finalPrice = finalPrice;
    this.quantity = quantity;
  }
}
