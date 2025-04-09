package com.p4zd4n.kebab.requests.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.OrderStatus;
import com.p4zd4n.kebab.enums.OrderType;
import com.p4zd4n.kebab.enums.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record NewOrderRequest(

        @JsonProperty("order_type")
        @NotNull(message = "{orderType.notNull}")
        OrderType orderType,

        @JsonProperty("order_status")
        @NotNull(message = "{orderStatus.notNull}")
        OrderStatus orderStatus,

        @JsonProperty("customer_phone")
        @Pattern(regexp = "^[0-9]{9}$", message = "{phone.invalidFormat}")
        String customerPhone,

        @JsonProperty("customer_email")
        @Email(message = "{email.invalidFormat}")
        String customerEmail,

        String street,

        @JsonProperty("house_number")
        Integer houseNumber,

        @JsonProperty("postal_code")
        String postalCode,

        String city,

        @JsonProperty("additional_comments")
        String additionalComments,

        Map<String, Map<Size, Integer>> meals,

        Map<String, Map<BigDecimal, Integer>> beverages,

        Map<String, Integer> addons
) {}
