package com.p4zd4n.kebab.requests.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.p4zd4n.kebab.enums.OrderStatus;
import com.p4zd4n.kebab.enums.OrderType;
import com.p4zd4n.kebab.enums.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Builder;

@Builder
public record UpdatedOrderRequest(
    @NotNull(message = "{id.notNull}") Long id,
    @JsonProperty("updated_order_type") OrderType orderType,
    @JsonProperty("updated_order_status") OrderStatus orderStatus,
    @JsonProperty("updated_customer_phone")
        @Pattern(regexp = "^[0-9]{9}$", message = "{phone.invalidFormat}")
        String customerPhone,
    @JsonProperty("updated_customer_email") @Email(message = "{email.invalidFormat}")
        String customerEmail,
    @JsonProperty("updated_street") String street,
    @JsonProperty("updated_house_number") Integer houseNumber,
    @JsonProperty("updated_postal_code") String postalCode,
    @JsonProperty("updated_city") String city,
    @JsonProperty("updated_additional_comments") String additionalComments,
    @JsonProperty("updated_meals") Map<String, Map<Size, Integer>> meals,
    @JsonProperty("updated_beverages") Map<String, Map<BigDecimal, Integer>> beverages,
    @JsonProperty("updated_addons") Map<String, Integer> addons) {}
