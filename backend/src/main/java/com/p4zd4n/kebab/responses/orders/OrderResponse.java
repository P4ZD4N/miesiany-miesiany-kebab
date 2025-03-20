package com.p4zd4n.kebab.responses.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.entities.Addon;
import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.enums.OrderStatus;
import com.p4zd4n.kebab.enums.OrderType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderResponse(

        Long id,
        @JsonProperty("order_type") OrderType orderType,
        @JsonProperty("order_status") OrderStatus orderStatus,
        @JsonProperty("customer_phone") String customerPhone,
        @JsonProperty("customer_email") String customerEmail,
        String street,
        @JsonProperty("house_number") String houseNumber,
        @JsonProperty("postal_code") String postalCode,
        String city,
        List<Meal> meals,
        List<Beverage> beverages,
        List<Addon> addons
) {}
