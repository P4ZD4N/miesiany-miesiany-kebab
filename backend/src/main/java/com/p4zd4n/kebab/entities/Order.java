package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.OrderStatus;
import com.p4zd4n.kebab.enums.OrderType;
import com.p4zd4n.kebab.enums.Size;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(name = "order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "street")
    private String street;

    @Column(name = "house_number")
    private Integer houseNumber;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "city")
    private String city;

    @Column(name = "additional_comments")
    private String additionalComments;

    @Column(name = "total_price")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderMeal> orderMeals = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderBeverage> orderBeverages = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderAddon> orderAddons = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderIngredient> orderIngredients = new ArrayList<>();

    @Builder
    public Order(OrderType orderType,
                 OrderStatus orderStatus,
                 String customerPhone,
                 String customerEmail,
                 String street,
                 Integer houseNumber,
                 String postalCode,
                 String city,
                 String additionalComments) {
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.additionalComments = additionalComments;
    }

    public void addMeal(Meal meal, Size size, Integer quantity) {
        OrderMeal orderMeal = OrderMeal.builder()
            .order(this)
            .mealName(meal.getName())
            .finalPrice(meal.getPriceForSizeWithDiscountIncluded(size, quantity))
            .size(size)
            .quantity(quantity)
            .ingredientNames(
                    meal.getMealIngredients().stream()
                        .map(mealIngredient -> mealIngredient.getIngredient().getName())
                        .collect(Collectors.toSet())
            )
            .build();

        orderMeals.add(orderMeal);
    }

    public void addBeverage(Beverage beverage, Integer quantity) {

        OrderBeverage orderBeverage = OrderBeverage.builder()
                .order(this)
                .beverageName(beverage.getName())
                .finalPrice(beverage.getPriceWithDiscountIncluded(quantity))
                .capacity(beverage.getCapacity())
                .quantity(quantity)
                .build();

        orderBeverages.add(orderBeverage);
    }

    public void addAddon(Addon addon, Integer quantity) {
        OrderAddon orderAddon = OrderAddon.builder()
                .order(this)
                .addonName(addon.getName())
                .finalPrice(addon.getPriceWithDiscountIncluded(quantity))
                .quantity(quantity)
                .build();
        orderAddons.add(orderAddon);
    }

    public void addIngredient(Ingredient ingredient, Integer quantity) {
        OrderIngredient orderIngredient = OrderIngredient.builder()
                .order(this)
                .ingredient(ingredient)
                .quantity(quantity)
                .build();
        orderIngredients.add(orderIngredient);
    }
}
