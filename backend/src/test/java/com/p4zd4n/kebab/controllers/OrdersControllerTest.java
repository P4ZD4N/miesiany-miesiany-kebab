package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.enums.OrderStatus;
import com.p4zd4n.kebab.enums.OrderType;
import com.p4zd4n.kebab.repositories.OrdersRepository;
import com.p4zd4n.kebab.responses.orders.OrderResponse;
import com.p4zd4n.kebab.services.orders.OrdersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdersController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrdersControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdersRepository ordersRepository;

    @MockBean
    private OrdersService ordersService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getOrders_ShouldReturnOrders_WhenCalled() throws Exception {

        List<OrderResponse> orders = Arrays.asList(
                OrderResponse.builder()
                        .orderType(OrderType.ON_SITE)
                        .orderStatus(OrderStatus.RECEIVED)
                        .customerPhone("123456789")
                        .customerEmail("example@example.com")
                        .build(),
                OrderResponse.builder()
                        .orderType(OrderType.ON_SITE)
                        .orderStatus(OrderStatus.RECEIVED)
                        .customerPhone("321456789")
                        .customerEmail("ex@example.com")
                        .build()
        );

        when(ordersService.getOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/v1/orders/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].customer_email", is("example@example.com")))
                .andExpect(jsonPath("$[0].customer_phone", is("123456789")))
                .andExpect(jsonPath("$[1].order_type", is("ON_SITE")))
                .andExpect(jsonPath("$[1].customer_phone", is("321456789")));

        verify(ordersService, times(1)).getOrders();
    }
}
