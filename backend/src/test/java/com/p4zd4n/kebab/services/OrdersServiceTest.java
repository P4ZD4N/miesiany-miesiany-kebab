package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.Order;
import com.p4zd4n.kebab.enums.OrderStatus;
import com.p4zd4n.kebab.enums.OrderType;
import com.p4zd4n.kebab.exceptions.expired.DiscountCodeExpiredException;
import com.p4zd4n.kebab.exceptions.expired.TrackOrderExpiredException;
import com.p4zd4n.kebab.exceptions.notfound.DiscountCodeNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.OrderNotFoundException;
import com.p4zd4n.kebab.exceptions.notmatches.TrackOrderDataDoesNotMatchException;
import com.p4zd4n.kebab.repositories.DiscountCodesRepository;
import com.p4zd4n.kebab.repositories.OrdersRepository;
import com.p4zd4n.kebab.requests.orders.NewOrderRequest;
import com.p4zd4n.kebab.requests.orders.TrackOrderRequest;
import com.p4zd4n.kebab.requests.orders.UpdatedOrderRequest;
import com.p4zd4n.kebab.responses.orders.NewOrderResponse;
import com.p4zd4n.kebab.responses.orders.OrderResponse;
import com.p4zd4n.kebab.responses.orders.RemovedOrderResponse;
import com.p4zd4n.kebab.responses.orders.UpdatedOrderResponse;
import com.p4zd4n.kebab.services.orders.OrdersService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private DiscountCodesRepository discountCodesRepository;

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

    @Test
    public void addOrder_ShouldAddOrder_WhenValidRequest() throws MessagingException {

        String language = "en";
        NewOrderRequest request = NewOrderRequest.builder()
                .orderStatus(OrderStatus.IN_PREPARATION)
                .orderType(OrderType.ON_SITE)
                .build();

        Order newOrder = Order.builder()
                .orderStatus(OrderStatus.IN_PREPARATION)
                .orderType(OrderType.ON_SITE)
                .build();
        newOrder.setId(1L);

        when(ordersRepository.save(any(Order.class))).thenReturn(newOrder);

        NewOrderResponse response = ordersService.addOrder(request, language);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully added new order with id '1'", response.message());

        verify(ordersRepository, times(3)).save(any(Order.class));
    }

    @Test
    public void addOrder_ShouldThrowDiscountCodeNotFoundException_WhenEnteredNotExistingDiscountCode() throws MessagingException {

        String language = "en";
        NewOrderRequest request = NewOrderRequest.builder()
                .orderStatus(OrderStatus.IN_PREPARATION)
                .orderType(OrderType.ON_SITE)
                .discountCode("kodzik1")
                .build();

        when(discountCodesRepository.findByCode("kodzik1")).thenThrow(new DiscountCodeNotFoundException("kodzik1"));

        DiscountCodeNotFoundException exception = assertThrows(DiscountCodeNotFoundException.class, () -> {
           ordersService.addOrder(request, language);
        });

        assertEquals("Discount code 'kodzik1' not found!", exception.getMessage());

        verify(discountCodesRepository, times(1)).findByCode("kodzik1");
    }

    @Test
    public void addOrder_ShouldThrowDiscountCodeExpiredException_WhenEnteredExpiredDiscountCode() throws MessagingException {

        String language = "en";
        NewOrderRequest request = NewOrderRequest.builder()
                .orderStatus(OrderStatus.IN_PREPARATION)
                .orderType(OrderType.ON_SITE)
                .discountCode("kodzik1")
                .build();

        when(discountCodesRepository.findByCode("kodzik1")).thenThrow(new DiscountCodeExpiredException("kodzik1"));

        DiscountCodeExpiredException exception = assertThrows(DiscountCodeExpiredException.class, () -> {
            ordersService.addOrder(request, language);
        });

        assertEquals("Discount code 'kodzik1' expired!", exception.getMessage());

        verify(discountCodesRepository, times(1)).findByCode("kodzik1");
    }

    @Test
    public void findOrderById_ShouldReturnOrder_WhenOrderExists() {

        Order order = Order.builder()
                .orderStatus(OrderStatus.IN_PREPARATION)
                .orderType(OrderType.ON_SITE)
                .build();
        order.setId(1L);

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        Order foundOrder = ordersService.findOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(OrderType.ON_SITE, foundOrder.getOrderType());
        assertEquals(OrderStatus.IN_PREPARATION, foundOrder.getOrderStatus());

        verify(ordersRepository, times(1)).findById(1L);
    }

    @Test
    public void findOrderById_ShouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {

        when(ordersRepository.findById(1L)).thenThrow(new OrderNotFoundException(1L));

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            ordersService.findOrderById(1L);
        });

        assertEquals("Order with id '1' not found!", exception.getMessage());

        verify(ordersRepository, times(1)).findById(1L);
    }

    @Test
    public void trackOrder_ShouldReturnOrder_WhenOrderExists() {

        TrackOrderRequest request = TrackOrderRequest.builder()
                .id(1L)
                .customerPhone("123456789")
                .build();

        Order order = Order.builder()
                .orderStatus(OrderStatus.IN_PREPARATION)
                .orderType(OrderType.ON_SITE)
                .customerPhone("123456789")
                .build();

        order.setId(1L);
        order.setUpdatedAt(LocalDateTime.now());

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = ordersService.trackOrder(request);

        assertNotNull(response);
        assertEquals("123456789", response.customerPhone());
        assertEquals(OrderType.ON_SITE, response.orderType());
        assertEquals(OrderStatus.IN_PREPARATION, response.orderStatus());

        verify(ordersRepository, times(1)).findById(1L);
    }

    @Test
    public void trackOrder_ShouldThrowOrderNotFoundException_WhenOrderNotExists() {

        TrackOrderRequest request = TrackOrderRequest.builder()
                .id(1L)
                .customerPhone("123456789")
                .build();

        when(ordersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> {
            ordersService.trackOrder(request);
        });

        verify(ordersRepository, times(1)).findById(1L);
    }

    @Test
    public void trackOrder_ShouldThrowTrackOrderDataDoesNotMatchException_WhenOrderDataNotMatch() {

        TrackOrderRequest request = TrackOrderRequest.builder()
                .id(1L)
                .customerPhone("123456789")
                .build();

        Order order = Order.builder()
                .orderStatus(OrderStatus.IN_PREPARATION)
                .orderType(OrderType.ON_SITE)
                .customerPhone("333333333")
                .build();

        order.setId(1L);
        order.setUpdatedAt(LocalDateTime.now());

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(TrackOrderDataDoesNotMatchException.class, () -> {
            ordersService.trackOrder(request);
        });

        verify(ordersRepository, times(1)).findById(1L);
    }

    @Test
    public void trackOrder_ShouldThrowTrackOrderExpiredException_WhenLastUpdateOverMaxTime() {

        TrackOrderRequest request = TrackOrderRequest.builder()
                .id(1L)
                .customerPhone("123456789")
                .build();

        Order order = Order.builder()
                .orderStatus(OrderStatus.IN_PREPARATION)
                .orderType(OrderType.ON_SITE)
                .customerPhone("123456789")
                .build();

        order.setId(1L);
        order.setUpdatedAt(LocalDateTime.now().minusMonths(5));

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(TrackOrderExpiredException.class, () -> {
            ordersService.trackOrder(request);
        });

        verify(ordersRepository, times(1)).findById(1L);
    }

    @Test
    public void updateOrder_ShouldUpdateOrder_WhenCalled() {

        Order order = Order.builder()
                .orderStatus(OrderStatus.IN_PREPARATION)
                .orderType(OrderType.ON_SITE)
                .customerPhone("123456789")
                .build();
        order.setId(1L);

        UpdatedOrderRequest request = UpdatedOrderRequest.builder()
                .id(1L)
                .orderStatus(OrderStatus.READY)
                .build();

        when(ordersRepository.save(any(Order.class))).thenReturn(order);

        UpdatedOrderResponse response = ordersService.updateOrder(order, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully updated order with id '1'", response.message());
        assertEquals(OrderStatus.READY, order.getOrderStatus());

        verify(ordersRepository, times(1)).save(order);
    }

    @Test
    public void removeOrder_ShouldRemoveOrder_WhenCalled() {

        Order order = Order.builder()
                .orderStatus(OrderStatus.IN_PREPARATION)
                .orderType(OrderType.ON_SITE)
                .customerPhone("123456789")
                .build();
        order.setId(1L);

        doNothing().when(ordersRepository).delete(order);

        RemovedOrderResponse response = ordersService.removeOrder(order);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully removed order with od '1'", response.message());

        verify(ordersRepository, times(1)).delete(order);
    }
}
