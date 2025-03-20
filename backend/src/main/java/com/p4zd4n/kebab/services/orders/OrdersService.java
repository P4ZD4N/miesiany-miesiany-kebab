package com.p4zd4n.kebab.services.orders;

import com.p4zd4n.kebab.entities.Order;
import com.p4zd4n.kebab.repositories.OrdersRepository;
import com.p4zd4n.kebab.responses.orders.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersService {

    private final OrdersRepository ordersRepository;

    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public List<OrderResponse> getOrders() {

        log.info("Started retrieving orders");

        List<Order> orders = ordersRepository.findAll();

        List<OrderResponse> response = orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved orders");

        return response;
    }

    public OrderResponse mapToResponse(Order order) {

        return OrderResponse.builder()
                .id(order.getId())
                .orderType(order.getOrderType())
                .orderStatus(order.getOrderStatus())
                .customerPhone(order.getCustomerPhone())
                .customerEmail(order.getCustomerEmail())
                .street(order.getStreet())
                .houseNumber(order.getHouseNumber())
                .postalCode(order.getPostalCode())
                .city(order.getCity())
                .meals(order.getMeals())
                .beverages(order.getBeverages())
                .addons(order.getAddons())
                .build();
    }
}
