package com.p4zd4n.kebab.responses.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.entities.*;
import com.p4zd4n.kebab.enums.OrderStatus;
import com.p4zd4n.kebab.enums.OrderType;
import jakarta.persistence.Column;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(

        Long id,
        @JsonProperty("order_type") OrderType orderType,
        @JsonProperty("order_status") OrderStatus orderStatus,
        @JsonProperty("customer_phone") String customerPhone,
        @JsonProperty("customer_email") String customerEmail,
        String street,
        @JsonProperty("house_number") Integer houseNumber,
        @JsonProperty("postal_code") String postalCode,
        String city,
        @JsonProperty("additional_comments") String additionalComments,
        @JsonProperty("total_price") BigDecimal totalPrice,
        @JsonProperty("created_at") LocalDateTime createdAt,
        List<OrderMeal> meals,
        List<OrderBeverage> beverages,
        List<OrderAddon> addons
) {}
