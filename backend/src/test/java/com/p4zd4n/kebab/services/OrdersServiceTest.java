package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.Order;
import com.p4zd4n.kebab.enums.OrderStatus;
import com.p4zd4n.kebab.enums.OrderType;
import com.p4zd4n.kebab.repositories.OrdersRepository;
import com.p4zd4n.kebab.responses.orders.OrderResponse;
import com.p4zd4n.kebab.services.orders.OrdersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @InjectMocks
    private OrdersService ordersService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getOrders_ShouldReturnOrders_WhenCalled() {

        List<Order> orders = Arrays.asList(
                Order.builder()
                        .orderType(OrderType.ON_SITE)
                        .orderStatus(OrderStatus.RECEIVED)
                        .customerPhone("123456789")
                        .customerEmail("example@example.com")
                        .build(),
                Order.builder()
                        .orderType(OrderType.ON_SITE)
                        .orderStatus(OrderStatus.RECEIVED)
                        .customerPhone("321456789")
                        .customerEmail("ex@example.com")
                        .build()
        );

        when(ordersRepository.findAll()).thenReturn(orders);

        List<OrderResponse> result = ordersService.getOrders();

        assertEquals(2, result.size());

        assertEquals(OrderStatus.RECEIVED, result.getFirst().orderStatus());
        assertEquals("example@example.com", result.getFirst().customerEmail());

        assertEquals("321456789", result.getLast().customerPhone());
        assertEquals("ex@example.com", result.getLast().customerEmail());

        verify(ordersRepository, times(1)).findAll();
    }
}
