package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.responses.orders.OrderResponse;
import com.p4zd4n.kebab.services.orders.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        log.info("Received get orders request");
        return ResponseEntity.ok(ordersService.getOrders());
    }
}
